package com.fuzzy.subsystem.frontend.remote.info;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;

/**
 * Created by kris on 02.11.16.
 */
public interface RControllerFrontendInfo extends QueryRemoteController {

	String getServerUrl() throws PlatformException;
}