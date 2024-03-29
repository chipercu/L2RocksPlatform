package com.fuzzy.subsystem.core.remote.mail;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;

public interface RControllerMailConfigGetter extends QueryRemoteController {

    Boolean isMailConfigured() throws PlatformException;

}