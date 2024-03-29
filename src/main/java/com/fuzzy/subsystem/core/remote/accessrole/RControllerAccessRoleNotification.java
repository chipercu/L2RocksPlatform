package com.fuzzy.subsystem.core.remote.accessrole;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

import java.util.HashSet;

public interface RControllerAccessRoleNotification extends QueryRemoteController {

    default void onBeforeRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException { }

    default void onAfterRemoveAccessRole(Long accessRoleId, ContextTransaction context) throws PlatformException { }

    default void onAfterEraseAccessRoleForEmployee(long accessRoleId, long employeeId, ContextTransaction context)
            throws PlatformException { }

    default void onAfterReplaceAccessRolesAtEmployee(HashSet<Long> newAccessRoleIds, long employeeId, ContextTransaction context)
            throws PlatformException { }
}
