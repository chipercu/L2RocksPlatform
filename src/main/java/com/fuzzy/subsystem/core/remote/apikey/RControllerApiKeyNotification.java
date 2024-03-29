package com.fuzzy.subsystem.core.remote.apikey;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerApiKeyNotification extends QueryRemoteController {

    void onBeforeRemoveApiKey(Long apiKeyId, ContextTransaction context) throws PlatformException;
}
