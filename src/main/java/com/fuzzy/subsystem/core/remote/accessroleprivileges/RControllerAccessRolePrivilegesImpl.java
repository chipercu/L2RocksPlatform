package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.accessroleprivilege.AccessRoleCorePrivilegeEditable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.servicemodesessionremover.ServiceModeSessionRemover;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeSetter;
import com.fuzzy.subsystems.entityprivilege.PrivilegeOperationsPair;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.List;

public class RControllerAccessRolePrivilegesImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerAccessRolePrivileges {

    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final EntityPrivilegeSetter<CorePrivilege, AccessRoleCorePrivilegeEditable> accessRolePrivilegeSetter;
    private final ServiceModeSessionRemover serviceModeSessionRemover;
    private final RCExecutor<RControllerAccessRolePrivilegesNotification> accessRolePrivilegesNotificator;

    public RControllerAccessRolePrivilegesImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        accessRolePrivilegeSetter = new EntityPrivilegeSetter<CorePrivilege, AccessRoleCorePrivilegeEditable>(
                AccessRoleCorePrivilegeEditable.class,
                resources
        ) {
            @Override
            protected void setEntityId(AccessRoleCorePrivilegeEditable object, long accessRoleId) {
                object.setAccessRoleId(accessRoleId);
            }

            @Override
            protected int getEntityFiledNumber() {
                return AccessRoleCorePrivilegeEditable.FIELD_ACCESS_ROLE_ID;
            }
        };
        serviceModeSessionRemover = new ServiceModeSessionRemover(resources);
        accessRolePrivilegesNotificator = new RCExecutor<>(resources, RControllerAccessRolePrivilegesNotification.class);
    }

    @Override
    public void setPrivilegesToAccessRole(long accessRoleId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException {
        new PrimaryKeyValidator(false)
                .validate(accessRoleId, accessRoleReadableResource, context.getTransaction());
        accessRolePrivilegesNotificator.exec(rc -> rc.onBeforeChangePrivileges(accessRoleId, context));
        List<PrivilegeOperationsPair<CorePrivilege>> privilegeOperationsPairs = new ArrayList<>();
        for (PrivilegeValue privilegeValue : privilegeValues) {
            CorePrivilege privilege = CorePrivilege.ofKey(privilegeValue.getKey());
            if (privilege != null) {
                privilegeOperationsPairs.add(new PrivilegeOperationsPair<>(privilege, privilegeValue.getOperations()));
            }
        }
        accessRolePrivilegeSetter.setPrivilegesToEntity(
                accessRoleId, privilegeOperationsPairs, context.getTransaction());
        clearSessionsForServiceMode(accessRoleId, context);
        accessRolePrivilegesNotificator.exec(rc -> rc.onAfterChangePrivileges(accessRoleId, context));
    }

    private void clearSessionsForServiceMode(long accessRoleId, ContextTransaction<?> context) throws PlatformException {
        FrontendSubsystem frontendSubsystem =
                Subsystems.getInstance().getCluster().getAnyLocalComponent(FrontendSubsystem.class);
        if (!frontendSubsystem.getConfig().isServiceMode()) {
            return;
        }
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
        employeeAccessRoleReadableResource.forEach(filter, employeeAccessRole ->
                        serviceModeSessionRemover.clearSessionForEmployeeIfNeed(
                                frontendSubsystem, employeeAccessRole.getEmployeeId(), context),
                context.getTransaction());
    }

}
