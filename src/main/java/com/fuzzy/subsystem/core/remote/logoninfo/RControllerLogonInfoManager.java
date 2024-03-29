package com.fuzzy.subsystem.core.remote.logoninfo;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

import java.time.Instant;

public interface RControllerLogonInfoManager extends QueryRemoteController {

    public void setLastLogonTime(long employeeId, Instant time, ContextTransaction context) throws PlatformException;

}