package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;
}
