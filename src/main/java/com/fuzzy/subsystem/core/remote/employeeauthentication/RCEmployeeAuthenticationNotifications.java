package com.fuzzy.subsystem.core.remote.employeeauthentication;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthenticationNotifications extends QueryRemoteController {

    void onAfterAssignAuthenticationToEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void onAfterEraseAuthenticationForEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;
}
