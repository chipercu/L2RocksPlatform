package com.fuzzy.subsystem.core.remote.serverinit;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RControllerServerInitNotification extends QueryRemoteController {

    void onServerInit(final ServerInitData data, ContextTransaction context)
            throws PlatformException;
}
