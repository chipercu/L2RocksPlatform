package com.fuzzy.subsystem.core.remote.authentication;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationEditable;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.remote.authenticationtype.RCAuthenticationType;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.resourceswithnotifications.RemovableResourceWithNotifications;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.utils.DomainObjectValidator;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RCAuthenticationImpl extends AbstractQueryRController<CoreSubsystem> implements RCAuthentication {

    private final RemovableResourceWithNotifications<
            AuthenticationEditable, RCAuthenticationNotifications> authenticationRemovableResource;
    private RCEmployeeAuthentication rcEmployeeAuthentication;
    private final RCExecutor<RCAuthenticationType> rcAuthenticationType;

    public RCAuthenticationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        authenticationRemovableResource = new RemovableResourceWithNotifications<>(
                resources, AuthenticationEditable.class, RCAuthenticationNotifications.class);
        rcEmployeeAuthentication =
                resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
        rcAuthenticationType = new RCExecutor<>(resources, RCAuthenticationType.class);
    }

    @Override
    public AuthenticationReadable create(AuthenticationCreatingBuilder builder,
                                         ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        validateName(builder.getName(), null, transaction);
        validateType(builder.getType(), context);
        if (Objects.equals(builder.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)) {
            HashFilter filter = new HashFilter(AuthenticationReadable.FIELD_TYPE, builder.getType());
            if (authenticationRemovableResource.find(filter, transaction) != null) {
                throw CoreExceptionBuilder.buildIntegratedAuthenticationAlreadyExistsException();
            }
        }
        AuthenticationEditable authentication = authenticationRemovableResource.create(transaction);
        authentication.setName(builder.getName());
        authentication.setType(builder.getType());
        authenticationRemovableResource.saveCreation(authentication, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_CREATE)
                        .withParam(CoreParameter.Authentication.NAME, builder.getName())
                        .withParam(CoreParameter.Authentication.TYPE, builder.getType()),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId()),
                context
        );
        return authentication;
    }

    @Override
    public AuthenticationReadable update(long authenticationId,
                                         AuthenticationUpdatingBuilder builder,
                                         ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AuthenticationEditable authentication = new PrimaryKeyValidator(false)
                .validateAndGet(authenticationId, authenticationRemovableResource, transaction);
        if (builder.getName().isPresent()) {
            validateName(builder.getName().get(), authenticationId, transaction);
            SecurityLog.info(
                    new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_UPDATE)
                            .withParam(CoreParameter.Authentication.NAME, builder.getName().get()),
                    new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId())
                            .withParam(CoreParameter.Authentication.OLD_NAME, authentication.getName())
                            .withParam(CoreParameter.Authentication.NEW_NAME, builder.getName().get()),
                    context
            );
            authentication.setName(builder.getName().get());
        }
        authenticationRemovableResource.saveUpdate(authentication, context);
        return authentication;
    }

    @Override
    public boolean remove(long authenticationId, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AuthenticationEditable authentication = authenticationRemovableResource.get(authenticationId, transaction);
        if (authentication == null) {
            return false;
        }
        int count = 0;
        try (IteratorEntity<AuthenticationEditable> ie = authenticationRemovableResource.iterator(transaction)) {
            while (ie.hasNext()) {
                ie.next();
                if (++count > 1) {
                    break;
                }
            }
        }
        if (count == 1) {
            throw CoreExceptionBuilder.buildLastAuthenticationException();
        }
        rcEmployeeAuthentication.clearAuthenticationForEmployees(authenticationId, context);
        authenticationRemovableResource.remove(authentication, context);
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Authentication.TYPE_REMOVE)
                        .withParam(CoreParameter.Authentication.NAME, authentication.getName()),
                new SyslogStructDataTarget(CoreTarget.TYPE_AUTHENTICATION, authentication.getId()),
                context
        );
        return true;
    }

    private void validateName(String name, Long currentId, QueryTransaction transaction) throws PlatformException {
        DomainObjectValidator.validateNonEmptyAndUnique(
                AuthenticationReadable.FIELD_NAME, name, currentId, authenticationRemovableResource, transaction);
    }

    private void validateType(String type, ContextTransaction<?> context) throws PlatformException {
        DomainObjectValidator.validateNonEmpty(AuthenticationReadable.FIELD_TYPE, type, AuthenticationReadable.class);
        List<String> types = rcAuthenticationType.apply(rc -> rc.getTypes(context), ArrayList::new);
        if (!types.contains(type)) {
            throw CoreExceptionBuilder.buildInvalidAuthenticationTypeException();
        }
    }
}
