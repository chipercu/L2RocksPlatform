package com.fuzzy.subsystem.core.remote.employeeauthenticationchecker;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;

import java.util.Objects;

public class RCEmployeeAuthenticationCheckerImpl extends AbstractQueryRController<CoreSubsystem>
        implements RCEmployeeAuthenticationChecker {

    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private final ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;

    public RCEmployeeAuthenticationCheckerImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
    }

    @Override
    public boolean isAssigned(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException {
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employeeId)
                .appendField(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, authenticationId);
        return employeeAuthenticationReadableResource.find(filter, context.getTransaction()) != null;
    }

    @Override
    public boolean isAssigned(String authenticationType, long employeeId, ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employeeId);
        try (IteratorEntity<EmployeeAuthenticationReadable> ie =
                     employeeAuthenticationReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                long authenticationId = ie.next().getAuthenticationId();
                AuthenticationReadable authentication = authenticationReadableResource.get(authenticationId, transaction);
                if (Objects.equals(authentication.getType(), authenticationType)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isAnyAuthenticationAssigned(long employeeId, ContextTransaction context) throws PlatformException {
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employeeId);
        return employeeAuthenticationReadableResource.find(filter, context.getTransaction()) != null;
    }
}
