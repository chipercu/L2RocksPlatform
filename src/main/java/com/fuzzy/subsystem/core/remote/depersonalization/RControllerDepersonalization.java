package com.fuzzy.subsystem.core.remote.depersonalization;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

public interface RControllerDepersonalization extends QueryRemoteController {

    void depersonalize(Options options) throws PlatformException;
}
