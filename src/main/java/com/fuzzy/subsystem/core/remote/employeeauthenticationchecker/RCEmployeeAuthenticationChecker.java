package com.fuzzy.subsystem.core.remote.employeeauthenticationchecker;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthenticationChecker extends QueryRemoteController {

    boolean isAssigned(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAssigned(String authenticationType, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAnyAuthenticationAssigned(long employeeId, ContextTransaction context) throws PlatformException;
}
