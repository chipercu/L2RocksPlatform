package com.fuzzy.subsystem.core.remote.employeeauthentication;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCEmployeeAuthenticationNotifications extends QueryRemoteController {

    void onAfterAssignAuthenticationToEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;

    void onAfterEraseAuthenticationForEmployee(long authenticationId, long employeeId, ContextTransaction context)
            throws PlatformException;
}
