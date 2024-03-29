package com.fuzzy.subsystem.core.remote.accessrole;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RControllerAccessRoleNotification extends QueryRemoteController {

    default void onBeforeRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException { }

    default void onAfterRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException { }

    default void onAfterEraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context)
            throws PlatformException { }

    default void onAfterReplaceAccessRolesAtEmployee(HashSet<Long> newAccessRoleIds, long employeeId, ContextTransaction context)
            throws PlatformException { }
}
