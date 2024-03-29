package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivileges extends QueryRemoteController {

    void setPrivilegesToAccessRole(long accessRoleId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException;
}
