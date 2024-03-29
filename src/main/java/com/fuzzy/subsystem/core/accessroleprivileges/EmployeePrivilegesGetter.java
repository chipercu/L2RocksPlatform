package com.fuzzy.subsystem.core.accessroleprivileges;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.function.BiFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeePrivilegesGetter {

    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final AccessRolePrivilegesGetter accessRolePrivilegesGetter;

    public EmployeePrivilegesGetter(ResourceProvider resources) {
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        accessRolePrivilegesGetter = new AccessRolePrivilegesGetter(resources);
    }

    public HashMap<String, AccessOperationCollection> getPrivileges(long employeeId, ContextTransaction<?> context)
            throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges = new HashMap<>();
        forEachPrivileges(employeeId, (privilege, operations) -> {
            AccessOperationCollection existingOperations = privileges.get(privilege);
            if (existingOperations == null) {
                privileges.put(privilege, operations);
            } else {
                existingOperations.addOperation(operations.getOperations());
            }
            return true;
        }, context);
        return privileges;
    }


    public boolean checkPrivilegeAccessOperations(
            long employeeId,
            String privilegeUniqueKey,
            List<AccessOperation> accessOperationList,
            ContextTransaction<?> context)
            throws PlatformException {

        boolean[] result = new boolean[]{ false };
        forEachPrivileges(employeeId, (privilege, operations) -> {
            if (privilege.equals(privilegeUniqueKey)) {
                result[0] = operations.contains(accessOperationList);
                return !result[0];
            }
            return true;
        }, context);


        final HashMap<String, AccessOperationCollection> privileges = getPrivileges(employeeId, context);
        return Optional.ofNullable(privileges.get(privilegeUniqueKey))
                .map(ac -> ac.contains(accessOperationList))
                .orElse(false);
    }


    public boolean isHaveAnyPrivileges(long employeeId, ContextTransaction<?> context) throws PlatformException {
        boolean[] result = new boolean[]{ false };
        forEachPrivileges(employeeId, (privilege, operations) -> {
            result[0] = !operations.isEmpty();
            return !result[0];
        }, context);
        return result[0];
    }

    private void forEachPrivileges(long employeeId,
                                   BiFunction<String, AccessOperationCollection, Boolean> handler,
                                   ContextTransaction<?> context) throws PlatformException {
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, employeeId);
        employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole -> {
            Long accessRoleId = employeeAccessRole.getAccessRoleId();
            HashMap<String, AccessOperationCollection> accessRolePrivileges =
                    accessRolePrivilegesGetter.getPrivileges(accessRoleId, context);
            for (Map.Entry<String, AccessOperationCollection> accessRolePrivilege : accessRolePrivileges.entrySet()) {
                if (!handler.apply(accessRolePrivilege.getKey(), accessRolePrivilege.getValue())) {
                    break;
                }
            }
        }, context.getTransaction());
    }
}
