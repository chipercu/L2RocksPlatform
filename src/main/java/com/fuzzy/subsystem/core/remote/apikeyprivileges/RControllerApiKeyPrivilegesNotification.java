package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyPrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;
}
