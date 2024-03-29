package com.fuzzy.subsystem.core.remote.licensedemployee;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.remote.licensebusinessrolechecker.RCLicenseBusinessRoleChecker;

import java.util.HashSet;

public class RCLicensedEmployeeImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicensedEmployee {
    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final RCLicenseBusinessRoleChecker rcLicenseBusinessRoleChecker;

    public RCLicensedEmployeeImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        rcLicenseBusinessRoleChecker = resources.getQueryRemoteController(CoreSubsystem.class, RCLicenseBusinessRoleChecker.class);
    }

    @Override
    public HashSet<Long> getLicensedEmployees(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException {
        HashSet<Long> targetEmployees = new HashSet<>();
        if (businessRoleLimit.equals(BusinessRoleLimit.ADMIN)) {
            HashSet<Long> adminRoles = new HashSet<>();
            QueryTransaction transaction = context.getTransaction();
            try (IteratorEntity<AccessRoleReadable> ie = accessRoleReadableResource.iterator(transaction)) {
                while (ie.hasNext()) {
                    AccessRoleReadable accessRoleReadable = ie.next();
                    if (rcLicenseBusinessRoleChecker.isAccessRoleMatchesBusinessRole(accessRoleReadable.getId(), BusinessRoleLimit.ADMIN, context)) {
                        adminRoles.add(accessRoleReadable.getId());
                    }
                }
            }
            for (Long accessRoleId : adminRoles) {
                employeeAccessRoleReadableResource.forEach(new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId),
                        employeeAccessRoleReadable -> targetEmployees.add(employeeAccessRoleReadable.getEmployeeId()),
                        transaction);
            }
        }
        return targetEmployees;
    }
}
