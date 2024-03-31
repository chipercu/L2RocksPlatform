package com.fuzzy.subsystem.core.remote.privilege;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.config.Language;
import com.fuzzy.subsystems.access.AccessOperationCollection;

public interface RControllerPrivilegeGetter extends QueryRemoteController {

    String getPrivilegeDisplayName(String privilegeKey, Language language, ContextTransaction context)
            throws PlatformException;

    AccessOperationCollection getAvailableOperations(String privilegeKey, ContextTransaction context)
            throws PlatformException;
}
