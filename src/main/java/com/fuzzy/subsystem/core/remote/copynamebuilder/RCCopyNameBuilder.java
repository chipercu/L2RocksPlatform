package com.fuzzy.subsystem.core.remote.copynamebuilder;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCCopyNameBuilder extends QueryRemoteController {

    void start(String sourceName, ContextTransaction context) throws PlatformException;

    String next() throws PlatformException;
}
