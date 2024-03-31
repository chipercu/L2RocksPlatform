package com.fuzzy.subsystems.remote;

import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.querypool.ResourceProvider;

public class RCExecutor<T extends QueryRemoteController> extends MultiExecutor<T> {

	public RCExecutor(ResourceProvider resources, Class<T> controllerClass) {
		super(resources.getQueryRemoteControllers(controllerClass));
	}
}
