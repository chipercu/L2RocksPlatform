package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivileges extends QueryRemoteController {

    void setPrivilegesToAccessRole(long accessRoleId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException;
}
