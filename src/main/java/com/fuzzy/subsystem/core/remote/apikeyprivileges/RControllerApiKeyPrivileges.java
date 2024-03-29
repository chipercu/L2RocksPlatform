package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;

public interface RControllerApiKeyPrivileges extends QueryRemoteController {

    void setPrivilegesToApiKey(long apiKeyId, PrivilegeValue[] privilegeValues, ContextTransaction context)
            throws PlatformException;
}
