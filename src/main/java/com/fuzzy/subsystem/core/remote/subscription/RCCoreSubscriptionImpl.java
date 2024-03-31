package com.fuzzy.subsystem.core.remote.subscription;

import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;

public class RCCoreSubscriptionImpl extends AbstractQueryRController<CoreSubsystem> implements RCCoreSubscription {

    private CoreSubsystem component;

    public RCCoreSubscriptionImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        this.component = component;
    }

    @Override
    public void sendEmployeeUpdateEvent(long employeeId, ContextTransaction context) {
        GEmployeeUpdateEvent.send(component, employeeId, context.getTransaction());
    }
}
