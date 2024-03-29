package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;

public interface RControllerApiKeyPrivileges extends QueryRemoteController {

    void setPrivilegesToApiKey(long apiKeyId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException;
}
