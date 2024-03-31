package com.fuzzy.subsystem.core.remote.serverinit;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RControllerServerInitNotification extends QueryRemoteController {

    void onServerInit(final ServerInitData data, ContextTransaction context)
            throws PlatformException;
}
