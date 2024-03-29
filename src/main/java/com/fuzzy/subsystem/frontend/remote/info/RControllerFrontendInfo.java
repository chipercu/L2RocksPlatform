package com.fuzzy.subsystem.frontend.remote.info;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

/**
 * Created by kris on 02.11.16.
 */
public interface RControllerFrontendInfo extends QueryRemoteController {

	String getServerUrl() throws PlatformException;
}