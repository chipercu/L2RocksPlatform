package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivileges extends QueryRemoteController {

    void setPrivilegesToAccessRole(long accessRoleId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException;
}
