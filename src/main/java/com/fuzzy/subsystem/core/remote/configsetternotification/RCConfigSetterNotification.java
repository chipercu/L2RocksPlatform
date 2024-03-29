package com.fuzzy.subsystem.core.remote.configsetternotification;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.io.Serializable;

public interface RCConfigSetterNotification extends QueryRemoteController {

    void onBeforeChangeConfig(String configName, Serializable newValue, ContextTransaction context) throws PlatformException;

    void onAfterChangeConfig(String configName, ContextTransaction context) throws PlatformException;
}
