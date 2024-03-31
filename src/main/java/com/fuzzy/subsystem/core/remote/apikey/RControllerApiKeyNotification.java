package com.fuzzy.subsystem.core.remote.apikey;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyNotification extends QueryRemoteController {

    void onBeforeRemoveApiKey(Long apiKeyId, ContextTransaction context) throws PlatformException;
}
