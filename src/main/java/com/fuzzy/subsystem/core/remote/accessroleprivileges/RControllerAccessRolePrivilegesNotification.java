package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerAccessRolePrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException;
}
