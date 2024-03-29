package com.fuzzy.subsystem.core.remote.systemevent;

import com.fuzzy.main.cluster.core.remote.AbstractRController;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;

public class RControllerSystemEventImpl extends AbstractRController<CoreSubsystem> implements RControllerSystemEvent {

    private final SystemEventService systemEventService;

    protected RControllerSystemEventImpl(CoreSubsystem component) {
        super(component);
        systemEventService = component.getSystemEventService();
    }

    @Override
    public void captureEvent(SystemEvent systemEvent) {
        systemEventService.captureEvent(systemEvent);
    }
}