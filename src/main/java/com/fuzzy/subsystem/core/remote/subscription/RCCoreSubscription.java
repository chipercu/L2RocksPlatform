package com.fuzzy.subsystem.core.remote.subscription;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;

public interface RCCoreSubscription extends QueryRemoteController {

    void sendEmployeeUpdateEvent(long employeeId, ContextTransaction context) throws PlatformException;
}
