package com.fuzzy.subsystem.core.remote.additionalfield;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldEditable;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueEditable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.remote.domainobjectnotifications.RCAdditionalFieldNotifications;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.resourceswithnotifications.RemovableResourceWithNotifications;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class RCAdditionalFieldImpl extends AbstractQueryRController<CoreSubsystem> implements RCAdditionalField {

    private final RemovableResourceWithNotifications<AdditionalFieldEditable, RCAdditionalFieldNotifications> additionalFieldRemovableResource;
    private final RemovableResource<AdditionalFieldValueEditable> additionalFieldValueRemovableResource;
    private final RCIntegrationsExecutor rcIntegrations;

    public RCAdditionalFieldImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        additionalFieldRemovableResource = new RemovableResourceWithNotifications<>(
                resources, AdditionalFieldEditable.class, RCAdditionalFieldNotifications.class);
        additionalFieldValueRemovableResource = resources.getRemovableResource(AdditionalFieldValueEditable.class);
        rcIntegrations = new RCIntegrationsExecutor(resources);
    }

    @Override
    public AdditionalFieldReadable create(AdditionalFieldCreatingBuilder builder, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (StringUtils.isEmpty(builder.getObjectType())) {
            throw CoreExceptionBuilder.buildEmptyFieldObjectException();
        }
        if (StringUtils.isEmpty(builder.getName())) {
            throw CoreExceptionBuilder.buildEmptyFieldNameException();
        }
        if (builder.getDataType() == null) {
            throw CoreExceptionBuilder.buildEmptyFieldDataTypeException();
        }

        if (builder.getListSource() == null) {
            if (builder.getDataType() == FieldDataType.ID) {
                throw CoreExceptionBuilder.buildEmptyFieldListSourceTypeException();
            }
        }

        String listSource = null;
        if(FieldDataType.ID == builder.getDataType()) {
            if (builder.getListSource() == null) {
                throw CoreExceptionBuilder.buildEmptyFieldListSourceTypeException();
            }
            listSource = builder.getListSource().getTable();
        }

        AdditionalFieldEditable additionalField = additionalFieldRemovableResource.create(transaction);
        additionalField.setObjectType(builder.getObjectType());
        additionalField.setKey(buildUniqueKey(builder.getObjectType(), builder.getName(), transaction));
        additionalField.setName(builder.getName());
        additionalField.setDataType(builder.getDataType());
        additionalField.setListSource(listSource);
        additionalField.setIndex(getNextIndex(builder.getObjectType(), transaction));
        additionalField.setOrder(getNextOrder(builder.getObjectType(), transaction));
        additionalFieldRemovableResource.saveCreation(additionalField, context);
        return additionalField;
    }

    @Override
    public AdditionalFieldReadable update(long additionalFieldId,
                                          AdditionalFieldUpdatingBuilder builder,
                                          ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AdditionalFieldEditable additionalField = new PrimaryKeyValidator(false)
                .validateAndGet(additionalFieldId, additionalFieldRemovableResource, transaction);
        if (rcIntegrations.isSynchronized(additionalField.getObjectType(), additionalField.getKey(), context)) {
            throw CoreExceptionBuilder.buildSynchronizedFieldException();
        }
        if (builder.getName().isPresent()) {
            if (StringUtils.isEmpty(builder.getName().get())) {
                throw CoreExceptionBuilder.buildEmptyFieldNameException();
            }
            additionalField.setName(builder.getName().get());
        }
        if (builder.getDataType().isPresent()) {
            if (builder.getDataType().get() == null) {
                throw CoreExceptionBuilder.buildEmptyFieldDataTypeException();
            }
            if (additionalField.getDataType() != builder.getDataType().get()) {
                additionalField.setDataType(builder.getDataType().get());
                HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId);
                additionalFieldValueRemovableResource.removeAll(filter, transaction);
            }
        }
        additionalFieldRemovableResource.saveUpdate(additionalField, context);
        return additionalField;
    }

    @Override
    public void order(ArrayList<Long> ids, ContextTransactionRequest context) throws PlatformException {
        HashSet<Long> processedIds = new HashSet<>();

        int order = 0;
        for (Long id : ids) {
            if (id == null) {
                continue;
            }

            final AdditionalFieldEditable additionalFieldEditable = additionalFieldRemovableResource.get(id, context.getTransaction());
            if (additionalFieldEditable == null) {
                throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(AdditionalFieldReadable.class, id);
            }

            additionalFieldEditable.setOrder(++order);
            additionalFieldRemovableResource.saveUpdate(additionalFieldEditable, context);

            processedIds.add(id);
        }

        try (IteratorEntity<AdditionalFieldEditable> ie = additionalFieldRemovableResource.iterator(context.getTransaction())) {
            while (ie.hasNext()) {
                final AdditionalFieldEditable entry = ie.next();
                if (!processedIds.contains(entry.getId())) {
                    entry.setOrder(++order);
                    additionalFieldRemovableResource.saveUpdate(entry, context);
                }
            }
        }
    }

    @Override
    public boolean remove(long additionalFieldId, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AdditionalFieldEditable additionalField = additionalFieldRemovableResource.get(additionalFieldId, transaction);
        if (additionalField == null) {
            return false;
        }
        if (rcIntegrations.isSynchronized(additionalField.getObjectType(), additionalField.getKey(), context)) {
            throw CoreExceptionBuilder.buildSynchronizedFieldException();
        }
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId);
        additionalFieldValueRemovableResource.removeAll(filter, transaction);
        additionalFieldRemovableResource.remove(additionalField, context);
        return true;
    }

    private String buildUniqueKey(String objectType, String baseKey, QueryTransaction transaction) throws PlatformException {
        String key = baseKey;
        for (int i = 0; ; i++) {
            HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, objectType)
                    .appendField(AdditionalFieldReadable.FIELD_KEY, key);
            if (additionalFieldRemovableResource.find(filter, transaction) == null) {
                break;
            }
            key = baseKey + i;
        }
        return key;
    }

    private int getNextIndex(String objectType, QueryTransaction transaction) throws PlatformException {
        int[] index = new int[] {-1};
        HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, objectType);
        additionalFieldRemovableResource.forEach(filter, additionalField -> {
            if (additionalField.getIndex() > index[0]) {
                index[0] = additionalField.getIndex();
            }
        }, transaction);
        return index[0] + 1;
    }

    private int getNextOrder(String objectType, QueryTransaction transaction) throws PlatformException {
        int[] order = new int[] {-1};
        HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, objectType);
        additionalFieldRemovableResource.forEach(filter, additionalField -> {
            if (additionalField.getOrder() > order[0]) {
                order[0] = additionalField.getOrder();
            }
        }, transaction);
        return order[0] + 1;
    }
}
