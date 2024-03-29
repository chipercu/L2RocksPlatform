package com.fuzzy.subsystem.core.remote.serverinit;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RControllerServerInitNotification extends QueryRemoteController {

    void onServerInit(final ServerInitData data, ContextTransaction context)
            throws PlatformException;
}
