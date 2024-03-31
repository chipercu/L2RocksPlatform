package com.fuzzy.subsystem.core.remote.copynamebuilder;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCCopyNameBuilder extends QueryRemoteController {

    void start(String sourceName, ContextTransaction context) throws PlatformException;

    String next() throws PlatformException;
}
