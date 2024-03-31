package com.fuzzy.subsystem.core.remote.employeeauthentication;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthentication extends QueryRemoteController {

    void assignAuthenticationToEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void eraseAuthenticationForEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void clearAuthenticationsForEmployee(long employeeId, ContextTransaction context) throws PlatformException;

    void clearAuthenticationForEmployees(long authenticationId, ContextTransaction context) throws PlatformException;
}
