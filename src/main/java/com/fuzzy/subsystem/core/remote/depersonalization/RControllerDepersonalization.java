package com.fuzzy.subsystem.core.remote.depersonalization;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;

public interface RControllerDepersonalization extends QueryRemoteController {

    void depersonalize(Options options) throws PlatformException;
}
