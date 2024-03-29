package com.fuzzy.subsystem.core.remote.logoninfo;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

import java.time.Instant;

public interface RControllerLogonInfoManager extends QueryRemoteController {

    public void setLastLogonTime(long employeeId, Instant time, ContextTransaction context) throws PlatformException;

}