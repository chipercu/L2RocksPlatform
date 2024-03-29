package com.fuzzy.subsystem.core.remote.apikey;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyNotification extends QueryRemoteController {

    void onBeforeRemoveApiKey(Long apiKeyId, ContextTransaction context) throws PlatformException;
}
