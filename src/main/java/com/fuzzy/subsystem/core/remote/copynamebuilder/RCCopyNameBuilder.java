package com.fuzzy.subsystem.core.remote.copynamebuilder;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCCopyNameBuilder extends QueryRemoteController {

    void start(String sourceName, ContextTransaction context) throws PlatformException;

    String next() throws PlatformException;
}
