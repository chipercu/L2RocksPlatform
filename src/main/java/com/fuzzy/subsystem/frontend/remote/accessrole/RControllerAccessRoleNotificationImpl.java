package com.fuzzy.subsystem.frontend.remote.accessrole;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.remote.accessrole.RControllerAccessRoleNotification;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege.AccessRoleFrontendPrivilegeEditable;
import com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege.AccessRoleFrontendPrivilegeReadable;
import com.fuzzy.subsystem.frontend.service.session.SessionServiceEmployee;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.HashMap;
import java.util.HashSet;

public class RControllerAccessRoleNotificationImpl extends AbstractQueryRController<FrontendSubsystem>
        implements RControllerAccessRoleNotification {

    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final RemovableResource<AccessRoleFrontendPrivilegeEditable> accessRoleFrontendPrivilegeRemovableResource;
    private final SessionServiceEmployee sessionService;
    private final EmployeePrivilegesGetter employeePrivilegesGetter;
    private HashSet<Long> employees = null;

    public RControllerAccessRoleNotificationImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        accessRoleFrontendPrivilegeRemovableResource = resources.getRemovableResource(AccessRoleFrontendPrivilegeEditable.class);
        sessionService = component.getSessionService();
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
    }

    @Override
    public void onBeforeRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        employees = new HashSet<>();
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                     employeeAccessRoleReadableResource.findAll(filter, context.getTransaction())) {
            while (ie.hasNext()) {
                employees.add(ie.next().getEmployeeId());
            }
        }
        removeAccessRoleFrontendPrivileges(accessRoleId, context.getTransaction());
    }

    @Override
    public void onAfterRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        if (employees != null) {
            for (Long employeeId : employees) {
                clearSessionForEmployee(employeeId, context);
            }
            employees = null;
        }
    }

    @Override
    public void onAfterEraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context)
            throws PlatformException {
        clearSessionForEmployee(employeeId, context);
    }

    @Override
    public void onAfterReplaceAccessRolesAtEmployee(
            HashSet<Long> newAccessRoleIds, long employeeId, ContextTransaction context) throws PlatformException {
        clearSessionForEmployee(employeeId, context);
    }

    private void removeAccessRoleFrontendPrivileges(Long accessRoleId, QueryTransaction transaction) throws PlatformException {
        final HashFilter filter = new HashFilter(AccessRoleFrontendPrivilegeReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        accessRoleFrontendPrivilegeRemovableResource.removeAll(filter, transaction);
    }

    private void clearSessionForEmployee(long employeeId, ContextTransaction<?> context) throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges =
                employeePrivilegesGetter.getPrivileges(employeeId, context);
        for (AccessOperationCollection operations : privileges.values()) {
            if (!operations.isEmpty()) {
                return;
            }
        }
        context.getTransaction().addCommitListener(() -> sessionService.clearSessions(employeeId, context));
    }
}