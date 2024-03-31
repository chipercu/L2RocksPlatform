package com.fuzzy.subsystem.frontend.remote.accessroleprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivilegesNotification;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege.AccessRoleFrontendPrivilegeEditable;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeSetter;
import com.fuzzy.subsystems.entityprivilege.PrivilegeOperationsPair;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RControllerAccessRolePrivilegesImpl extends AbstractQueryRController<FrontendSubsystem> implements RControllerAccessRolePrivileges {

    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final EntityPrivilegeSetter<FrontendPrivilege, AccessRoleFrontendPrivilegeEditable> accessRoleFrontendPrivilegeSetter;
    private final RCExecutor<RControllerAccessRolePrivilegesNotification> accessRolePrivilegesNotificator;

    public RControllerAccessRolePrivilegesImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        accessRoleFrontendPrivilegeSetter = new EntityPrivilegeSetter<FrontendPrivilege, AccessRoleFrontendPrivilegeEditable>(AccessRoleFrontendPrivilegeEditable.class, resources) {
            @Override
            protected void setEntityId(AccessRoleFrontendPrivilegeEditable object, long accessRoleId) {
                object.setAccessRoleId(accessRoleId);
            }

            @Override
            protected int getEntityFiledNumber() {
                return AccessRoleFrontendPrivilegeEditable.FIELD_ACCESS_ROLE_ID;
            }
        };
        accessRolePrivilegesNotificator = new RCExecutor<>(resources, RControllerAccessRolePrivilegesNotification.class);
    }

    @Override
    public void setPrivilegesToAccessRole(long accessRoleId, PrivilegeValue[] privilegeValues, ContextTransaction context) throws PlatformException {
        new PrimaryKeyValidator(false).validate(accessRoleId, accessRoleReadableResource, context.getTransaction());
        accessRolePrivilegesNotificator.exec(rc -> rc.onBeforeChangePrivileges(accessRoleId, context));
        List<PrivilegeOperationsPair<FrontendPrivilege>> frontendPrivilegeOperationsPairs = new ArrayList<>();
        for (PrivilegeValue privilegeValue : privilegeValues) {
            final FrontendPrivilege privilege = FrontendPrivilege.ofKey(privilegeValue.getKey());
            if (Objects.nonNull(privilege)) {
                frontendPrivilegeOperationsPairs.add(new PrivilegeOperationsPair<>(privilege, privilegeValue.getOperations()));
            }
        }
        accessRoleFrontendPrivilegeSetter.setPrivilegesToEntity(accessRoleId, frontendPrivilegeOperationsPairs, context.getTransaction());
        accessRolePrivilegesNotificator.exec(rc -> rc.onAfterChangePrivileges(accessRoleId, context));
    }
}
