package com.fuzzy.subsystems.remote;

import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.querypool.ResourceProvider;

public class RCExecutor<T extends QueryRemoteController> extends MultiExecutor<T> {

	public RCExecutor(ResourceProvider resources, Class<T> controllerClass) {
		super(resources.getQueryRemoteControllers(controllerClass));
	}
}
