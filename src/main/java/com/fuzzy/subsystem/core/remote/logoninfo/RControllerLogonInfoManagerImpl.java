package com.fuzzy.subsystem.core.remote.logoninfo;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.logoninfo.LogonInfoManager;

import java.time.Instant;

public class RControllerLogonInfoManagerImpl extends AbstractQueryRController<CoreSubsystem>
		implements RControllerLogonInfoManager {

	private final LogonInfoManager logonInfoManager;

	public RControllerLogonInfoManagerImpl(CoreSubsystem component, ResourceProvider resources) {
		super(component, resources);
		logonInfoManager = new LogonInfoManager(resources);
	}

	@Override
	public void setLastLogonTime(long employeeId, Instant time, ContextTransaction context) throws PlatformException {
		logonInfoManager.setLastLogonTime(employeeId, time, context);
	}
}
