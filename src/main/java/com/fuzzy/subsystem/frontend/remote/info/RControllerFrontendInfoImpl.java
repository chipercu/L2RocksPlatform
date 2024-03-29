package com.fuzzy.subsystem.frontend.remote.info;

import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;

/**
 * Created by kris on 02.11.16.
 */
public class RControllerFrontendInfoImpl extends AbstractQueryRController<FrontendSubsystem>
        implements RControllerFrontendInfo {

    public RControllerFrontendInfoImpl(FrontendSubsystem subSystem, ResourceProvider resources) {
        super(subSystem, resources);
	}

	@Override
	public String getServerUrl() {
		return component.getConfig().getUrl().toExternalForm();
	}
}
