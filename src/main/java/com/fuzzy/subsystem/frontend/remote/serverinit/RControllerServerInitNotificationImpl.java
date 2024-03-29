package com.fuzzy.subsystem.frontend.remote.serverinit;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.RControllerAccessRolePrivileges;
import com.fuzzy.subsystem.core.remote.serverinit.RControllerServerInitNotification;
import com.fuzzy.subsystem.core.remote.serverinit.ServerInitData;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;

public class RControllerServerInitNotificationImpl extends AbstractQueryRController<FrontendSubsystem> implements RControllerServerInitNotification {

    private final RControllerAccessRolePrivileges rControllerAccessRolePrivileges;

    public RControllerServerInitNotificationImpl(FrontendSubsystem component, ResourceProvider resources) {
        super(component, resources);
        rControllerAccessRolePrivileges = resources.getQueryRemoteController(FrontendSubsystem.class, RControllerAccessRolePrivileges.class);
    }

    @Override
    public void onServerInit(ServerInitData data, ContextTransaction context) throws PlatformException {
        docsAccessAdminAccessRoleInit(data.getAdministratorAccessRoleId(), context);
    }

    private void docsAccessAdminAccessRoleInit(long accessRoleId, ContextTransaction<?> context) throws PlatformException {
        PrivilegeValue[] privilegeValues = new PrivilegeValue[]{ new PrivilegeValue(FrontendPrivilege.DOCUMENTATION_ACCESS.getUniqueKey(),
                new AccessOperationCollection(AccessOperation.READ)) };
        rControllerAccessRolePrivileges.setPrivilegesToAccessRole(accessRoleId, privilegeValues, context);
    }
}
