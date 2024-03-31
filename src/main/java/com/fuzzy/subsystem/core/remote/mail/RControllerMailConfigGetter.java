package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

public interface RControllerMailConfigGetter extends QueryRemoteController {

    Boolean isMailConfigured() throws PlatformException;

}