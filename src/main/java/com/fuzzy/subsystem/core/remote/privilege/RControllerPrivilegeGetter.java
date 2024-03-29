package com.fuzzy.subsystem.core.remote.privilege;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.access.AccessOperationCollection;

public interface RControllerPrivilegeGetter extends QueryRemoteController {

    String getPrivilegeDisplayName(String privilegeKey, Language language, ContextTransaction context)
            throws PlatformException;

    AccessOperationCollection getAvailableOperations(String privilegeKey, ContextTransaction context)
            throws PlatformException;
}
