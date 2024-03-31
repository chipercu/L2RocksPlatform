package com.fuzzy.subsystem.core.remote.logoninfo;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

import java.time.Instant;

public interface RControllerLogonInfoManager extends QueryRemoteController {

    public void setLastLogonTime(long employeeId, Instant time, ContextTransaction context) throws PlatformException;

}