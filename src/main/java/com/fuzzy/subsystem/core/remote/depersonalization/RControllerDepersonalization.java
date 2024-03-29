package com.fuzzy.subsystem.core.remote.depersonalization;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

public interface RControllerDepersonalization extends QueryRemoteController {

    void depersonalize(Options options) throws PlatformException;
}
