package com.fuzzy.subsystem.core.remote.employeeauthentication;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationEditable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.utils.LastAdministratorValidator;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

public class RCEmployeeAuthenticationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RCEmployeeAuthentication {

    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final RemovableResource<EmployeeAuthenticationEditable> employeeAuthenticationRemovableResource;
    private final RCExecutor<RCEmployeeAuthenticationNotifications> rcEmployeeAuthenticationNotifications;
    private final LastAdministratorValidator lastAdministratorValidator;

    public RCEmployeeAuthenticationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeeAuthenticationRemovableResource = resources.getRemovableResource(EmployeeAuthenticationEditable.class);
        rcEmployeeAuthenticationNotifications = new RCExecutor<>(resources, RCEmployeeAuthenticationNotifications.class);
        lastAdministratorValidator = new LastAdministratorValidator(component, resources);
    }

    @Override
    public void assignAuthenticationToEmployee(long authenticationId,
                                               long employeeId,
                                               ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = getFilter(authenticationId, employeeId, transaction);
        if (employeeAuthenticationRemovableResource.find(filter, transaction) == null) {
            EmployeeAuthenticationEditable employeeAuthentication =
                    employeeAuthenticationRemovableResource.create(transaction);
            employeeAuthentication.setAuthenticationId(authenticationId);
            employeeAuthentication.setEmployeeId(employeeId);
            employeeAuthenticationRemovableResource.save(employeeAuthentication, transaction);
            rcEmployeeAuthenticationNotifications.exec(rc ->
                    rc.onAfterAssignAuthenticationToEmployee(authenticationId, employeeId, context));
        }
    }

    @Override
    public void eraseAuthenticationForEmployee(long authenticationId,
                                               long employeeId,
                                               ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = getFilter(authenticationId, employeeId, transaction);
        EmployeeAuthenticationEditable employeeAuthentication =
                employeeAuthenticationRemovableResource.find(filter, transaction);
        if (employeeAuthentication != null) {
            employeeAuthenticationRemovableResource.remove(employeeAuthentication, transaction);
            rcEmployeeAuthenticationNotifications.exec(rc ->
                    rc.onAfterEraseAuthenticationForEmployee(authenticationId, employeeId, context));
        }
        lastAdministratorValidator.validate(context);
    }

    @Override
    public void clearAuthenticationsForEmployee(long employeeId, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        new PrimaryKeyValidator(false).validate(employeeId, employeeReadableResource, transaction);
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employeeId);
        employeeAuthenticationRemovableResource.forEach(filter, employeeAuthentication -> {
            employeeAuthenticationRemovableResource.remove(employeeAuthentication, transaction);
            rcEmployeeAuthenticationNotifications.exec(rc -> rc.onAfterEraseAuthenticationForEmployee(
                    employeeAuthentication.getAuthenticationId(), employeeId, context));
        }, transaction);
        lastAdministratorValidator.validate(context);
    }

    @Override
    public void clearAuthenticationForEmployees(long authenticationId,
                                                ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        new PrimaryKeyValidator(false)
                .validate(authenticationId, authenticationReadableResource, transaction);
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, authenticationId);
        employeeAuthenticationRemovableResource.forEach(filter, employeeAuthentication -> {
            employeeAuthenticationRemovableResource.remove(employeeAuthentication, transaction);
            rcEmployeeAuthenticationNotifications.exec(rc -> rc.onAfterEraseAuthenticationForEmployee(
                    authenticationId, employeeAuthentication.getEmployeeId(), context));
        }, transaction);
        lastAdministratorValidator.validate(context);
    }

    private HashFilter getFilter(long authenticationId,
                                 long employeeId,
                                 QueryTransaction transaction) throws PlatformException {
        PrimaryKeyValidator pkValidator = new PrimaryKeyValidator(false);
        pkValidator.validate(authenticationId, authenticationReadableResource, transaction);
        pkValidator.validate(employeeId, employeeReadableResource, transaction);
        return new HashFilter(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, authenticationId)
                .appendField(EmployeeAuthenticationEditable.FIELD_EMPLOYEE_ID, employeeId);
    }
}
