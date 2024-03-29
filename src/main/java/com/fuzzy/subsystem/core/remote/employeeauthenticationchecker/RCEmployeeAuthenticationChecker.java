package com.fuzzy.subsystem.core.remote.employeeauthenticationchecker;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthenticationChecker extends QueryRemoteController {

    boolean isAssigned(long authenticationId, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAssigned(String authenticationType, long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAnyAuthenticationAssigned(long employeeId, ContextTransaction context) throws PlatformException;
}
