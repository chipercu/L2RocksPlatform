package com.fuzzy.subsystem.core.remote.mail;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

public interface RControllerMailConfigGetter extends QueryRemoteController {

    Boolean isMailConfigured() throws PlatformException;

}