package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;
}
