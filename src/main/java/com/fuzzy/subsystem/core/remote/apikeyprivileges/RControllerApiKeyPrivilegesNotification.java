package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyPrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;
}
