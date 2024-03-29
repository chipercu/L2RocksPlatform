package com.fuzzy.subsystem.core.remote.subscription;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;

public interface RCCoreSubscription extends QueryRemoteController {

    void sendEmployeeUpdateEvent(long employeeId, ContextTransaction context) throws PlatformException;
}
