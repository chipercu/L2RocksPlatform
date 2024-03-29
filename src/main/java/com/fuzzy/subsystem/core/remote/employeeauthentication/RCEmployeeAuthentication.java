package com.fuzzy.subsystem.core.remote.employeeauthentication;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthentication extends QueryRemoteController {

    void assignAuthenticationToEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void eraseAuthenticationForEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void clearAuthenticationsForEmployee(long employeeId, ContextTransaction context) throws PlatformException;

    void clearAuthenticationForEmployees(long authenticationId, ContextTransaction context) throws PlatformException;
}
