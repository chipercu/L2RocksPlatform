package com.fuzzy.subsystem.core.remote.subscription;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;

public interface RCCoreSubscription extends QueryRemoteController {

    void sendEmployeeUpdateEvent(long employeeId, ContextTransaction context) throws PlatformException;
}
