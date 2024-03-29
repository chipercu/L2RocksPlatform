package com.fuzzy.subsystem.core.remote.employeeauthenticationchecker;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthenticationChecker extends QueryRemoteController {

    boolean isAssigned(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAssigned(String authenticationType, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAnyAuthenticationAssigned(long employeeId, ContextTransaction context) throws PlatformException;
}
