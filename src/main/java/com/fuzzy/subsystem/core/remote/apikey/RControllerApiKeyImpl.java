package com.fuzzy.subsystem.core.remote.apikey;

import com.google.common.base.Objects;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyEditable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyTypes;
import com.fuzzy.subsystem.core.remote.crypto.RCCrypto;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.GeneralEvent;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.ApiKeyUtils;
import com.fuzzy.subsystems.utils.DomainObjectValidator;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RControllerApiKeyImpl extends AbstractQueryRController<CoreSubsystem> implements RControllerApiKey {

    private final RCCrypto rcCrypto;
    private RemovableResource<ApiKeyEditable> apiKeyRemovableResource;
    private Set<RControllerApiKeyNotification> rControllerApiKeyNotifications;

    public RControllerApiKeyImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        apiKeyRemovableResource = resources.getRemovableResource(ApiKeyEditable.class);
        rControllerApiKeyNotifications = resources.getQueryRemoteControllers(RControllerApiKeyNotification.class);
        rcCrypto = resources.getQueryRemoteController(CoreSubsystem.class, RCCrypto.class);
    }

    @Override
    public ApiKeyReadable create(ApiKeyBuilder builder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (!builder.isContainsName()) {
            throw GeneralExceptionBuilder.buildEmptyValueException(
                    ApiKeyReadable.class, ApiKeyReadable.FIELD_NAME);
        }
        ApiKeyEditable apiKey = apiKeyRemovableResource.create(transaction);
        setFieldsFor(apiKey, builder, context);
        if (!builder.isContainsType()) {
            apiKey.setType(ApiKeyTypes.NONE);
        }
        if (Objects.equal(apiKey.getType(), ApiKeyTypes.CERTIFICATE.getType())) {
            if (!builder.isContainsValue()) {
                throw GeneralExceptionBuilder.buildEmptyValueException(ApiKeyReadable.class, ApiKeyReadable.FIELD_VALUE);
            }
        } else if (!builder.isContainsValue()) {
            String value = null;
            while (value == null) {
                value = UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
                HashFilter filter = new HashFilter(ApiKeyReadable.FIELD_VALUE, value);
                if (apiKeyRemovableResource.find(filter, transaction) != null) {
                    value = null;
                }
            }
            apiKey.setValue(value);
        }
        apiKeyRemovableResource.save(apiKey, transaction);

        SecurityLog.info(
                new SyslogStructDataEvent(GeneralEvent.TYPE_CREATE)
                        .withParam(GeneralEvent.PARAM_NAME, apiKey.getName()),
                new SyslogStructDataTarget(CoreTarget.TYPE_API_KEY, apiKey.getId())
                        .withParam(GeneralEvent.PARAM_VALUE, ApiKeyUtils.concealmentApiKey(apiKey.getValue()))
                        .withParam(GeneralEvent.PARAM_NAME, apiKey.getName()),
                context
        );

        return apiKey;
    }

    @Override
    public ApiKeyReadable update(long apiKeyId, ApiKeyBuilder builder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        ApiKeyEditable apiKey = new PrimaryKeyValidator(false)
                .validateAndGet(apiKeyId, apiKeyRemovableResource, transaction);
        if (builder.isContainsType()) {
            throw GeneralExceptionBuilder.buildInvalidValueException(
                    ApiKeyReadable.class, ApiKeyReadable.FIELD_TYPE, builder.getType().getType());
        }
        String prevName = apiKey.getName();
        setFieldsFor(apiKey, builder, context);
        apiKeyRemovableResource.save(apiKey, transaction);
        if (builder.isContainsName() && !prevName.equals(builder.getName())) {
            SecurityLog.info(
                    new SyslogStructDataEvent(GeneralEvent.TYPE_UPDATE)
                            .withParam(GeneralEvent.PARAM_OLD_NAME, prevName)
                            .withParam(GeneralEvent.PARAM_NEW_NAME, builder.getName()),
                    new SyslogStructDataTarget(CoreTarget.TYPE_API_KEY, apiKey.getId())
                            .withParam(GeneralEvent.PARAM_NAME, prevName),
                    context
            );
        }
        return apiKey;
    }

    @Override
    public HashSet<Long> remove(HashSet<Long> apiKeyIds, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashSet<Long> removed = new HashSet<>();
        for (Long apiKeyId : apiKeyIds) {
            if (apiKeyId == null) {
                continue;
            }
            ApiKeyEditable apiKey = apiKeyRemovableResource.get(apiKeyId, transaction);
            if (apiKey != null) {
                for (RControllerApiKeyNotification rControllerApiKeyNotification : rControllerApiKeyNotifications) {
                    rControllerApiKeyNotification.onBeforeRemoveApiKey(apiKeyId, context);
                }
                apiKeyRemovableResource.remove(apiKey, transaction);
                removed.add(apiKeyId);

                SecurityLog.info(
                        new SyslogStructDataEvent(GeneralEvent.TYPE_REMOVE),
                        new SyslogStructDataTarget(CoreTarget.TYPE_API_KEY, apiKey.getId())
                                .withParam(GeneralEvent.PARAM_VALUE, ApiKeyUtils.concealmentApiKey(apiKey.getValue()))
                                .withParam(GeneralEvent.PARAM_NAME, apiKey.getName()),
                        context
                );
            }
        }
        return removed;
    }

    private void setFieldsFor(
            ApiKeyEditable targetApiKey,
            ApiKeyBuilder builder,
            ContextTransaction context
    ) throws PlatformException {
        if (builder.isContainsName()) {
            DomainObjectValidator.validateNonEmptyAndUnique(
                    ApiKeyReadable.FIELD_NAME,
                    builder.getName(),
                    targetApiKey.getId(),
                    apiKeyRemovableResource,
                    context.getTransaction()
            );
            targetApiKey.setName(builder.getName());
        }
        if (builder.isContainsType()) {
            targetApiKey.setType(builder.getType());
        }
        if (builder.isContainsValue()) {
            if (StringUtils.isEmpty(builder.getValue())) {
                throw GeneralExceptionBuilder.buildEmptyValueException(
                        ApiKeyReadable.class, ApiKeyReadable.FIELD_VALUE);
            }
            checkUniqueValue(targetApiKey.getId(), builder.getValue(), context.getTransaction());
            targetApiKey.setValue(builder.getValue());
        }
        if (builder.isContainsContent()) {
            byte[] content = rcCrypto.encrypt(builder.getContent(), context);
            targetApiKey.setContent(content);
        }
    }

    private void checkUniqueValue(Long excludedId, String value, QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(ApiKeyReadable.FIELD_VALUE, value);
        try (IteratorEntity<ApiKeyEditable> ie = apiKeyRemovableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                if (!Objects.equal(excludedId, ie.next().getId())) {
                    throw GeneralExceptionBuilder.buildNotUniqueValueException(
                            ApiKeyReadable.class, ApiKeyReadable.FIELD_VALUE, value);
                }
            }
        }
    }
}
