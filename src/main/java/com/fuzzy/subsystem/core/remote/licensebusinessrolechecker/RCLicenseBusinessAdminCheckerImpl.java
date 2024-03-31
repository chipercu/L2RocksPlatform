package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.accessroleprivileges.AccessRolePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.remote.licenserequirements.RCLicenseRequirementsGetter;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.HashMap;
import java.util.Map;

public class RCLicenseBusinessAdminCheckerImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicenseBusinessAdminChecker {
    private final PrimaryKeyValidator primaryKeyValidator;
    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final AccessRolePrivilegesGetter accessRolePrivilegesGetter;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final RCLicenseRequirementsGetter rcLicenseRequirementsGetter;

    public RCLicenseBusinessAdminCheckerImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        primaryKeyValidator = new PrimaryKeyValidator(false);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        accessRolePrivilegesGetter = new AccessRolePrivilegesGetter(resources);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        rcLicenseRequirementsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicenseRequirementsGetter.class);
    }

    @Override
    public boolean isBusinessAdminRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        primaryKeyValidator.validateAndGet(accessRoleId, accessRoleReadableResource, context.getTransaction());
        HashMap<String, AccessOperationCollection> privileges = accessRolePrivilegesGetter.getPrivileges(accessRoleId, context);
        for (Map.Entry<String, AccessOperationCollection> entry : privileges.entrySet()) {
            String privilegeName = entry.getKey();
            if (!rcLicenseRequirementsGetter.getNonBusinessAdminPrivileges(context).contains(privilegeName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBusinessAdminEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeId);
        try (IteratorEntity<EmployeeAccessRoleReadable> ie = employeeAccessRoleReadableResource.findAll(filter, context.getTransaction())) {
            while (ie.hasNext()) {
                EmployeeAccessRoleReadable employeeAccessRoleReadable = ie.next();
                boolean adminRole = isBusinessAdminRole(employeeAccessRoleReadable.getAccessRoleId(), context);
                if (adminRole) {
                    return true;
                }
            }
        }
        return false;
    }


}
