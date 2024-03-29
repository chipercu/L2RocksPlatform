package com.fuzzy.subsystem.core.remote.configsetternotification;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

import java.io.Serializable;

public interface RCConfigSetterNotification extends QueryRemoteController {

    void onBeforeChangeConfig(String configName, Serializable newValue, ContextTransaction context) throws PlatformException;

    void onAfterChangeConfig(String configName, ContextTransaction context) throws PlatformException;
}
