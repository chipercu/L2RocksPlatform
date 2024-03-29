package com.fuzzy.subsystem.core.remote.apikeyprivileges;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyPrivilegesNotification extends QueryRemoteController {

    void onBeforeChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;

    void onAfterChangePrivileges(long apiKeyId, ContextTransaction context) throws PlatformException;
}
