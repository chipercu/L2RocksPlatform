package com.fuzzy.subsystem.core.utils;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.config.LogonType;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LastAdministratorValidator {

    private final CoreSubsystem coreSubsystem;
    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private final ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;

    public LastAdministratorValidator(CoreSubsystem coreSubsystem, ResourceProvider resources) {
        this.coreSubsystem = coreSubsystem;
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
    }

    public Set<Long> getAdministrators(ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Set<Long> administrators = new HashSet<>();
        HashFilter aFilter = new HashFilter(AccessRoleReadable.FIELD_ADMIN, true);
        try (IteratorEntity<AccessRoleReadable> aIter = accessRoleReadableResource.findAll(aFilter, transaction)) {
            while (aIter.hasNext()) {
                long accessRoleId = aIter.next().getId();
                HashFilter eaFilter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
                try (IteratorEntity<EmployeeAccessRoleReadable> eaIter =
                             employeeAccessRoleReadableResource.findAll(eaFilter, transaction)) {
                    while (eaIter.hasNext()) {
                        long employeeId = eaIter.next().getEmployeeId();
                        if (isEnabledAuth(employeeId, transaction)) {
                            administrators.add(employeeId);
                        }
                    }
                }
            }
        }
        return administrators;
    }

    public void validate(ContextTransaction<?> context) throws PlatformException {
        if (getAdministrators(context).isEmpty()) {
            throw CoreExceptionBuilder.buildLastAdministratorException();
        }
    }

    private boolean isEnabledAuth(long employeeId, QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, employeeId);
        try (IteratorEntity<EmployeeAuthenticationReadable> ie =
                     employeeAuthenticationReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                AuthenticationReadable authentication =
                        authenticationReadableResource.get(ie.next().getAuthenticationId(), transaction);
                if (!Objects.equals(authentication.getType(), CoreSubsystemConsts.AuthenticationTypes.INTEGRATED)
                        || checkIntegratedAuthentication(employeeId, transaction)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIntegratedAuthentication(long employeeId, QueryTransaction transaction) throws PlatformException {
        EmployeeReadable employee = employeeReadableResource.get(employeeId, transaction);
        String authFieldValue = coreSubsystem.getConfig().getLogonType() == LogonType.LOGIN ?
                employee.getLogin() : employee.getEmail();
        return !StringUtils.isEmpty(authFieldValue) && employee.getPasswordHash() != null;
    }
}
