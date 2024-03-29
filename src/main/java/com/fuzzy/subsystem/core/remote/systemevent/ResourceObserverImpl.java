package com.fuzzy.subsystem.core.remote.systemevent;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.observer.ResourceObserver;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;

public class ResourceObserverImpl implements ResourceObserver {
    private final RControllerSystemEvent controllerSystemEvent;

    public ResourceObserverImpl(CoreSubsystem coreSubsystem) {
        controllerSystemEvent = coreSubsystem.getRemotes().get(CoreSubsystem.class, RControllerSystemEvent.class);
    }

    @Override
    public void execute(String eventType,
                        String subsystemUuid,
                        String message,
                        Duration ttl,
                        ResourceMonitorStatus status,
                        HashMap<String, Serializable> params) throws PlatformException {
        controllerSystemEvent.captureEvent(SystemEvent.newBuilder()
                .withEventType(eventType)
                .withLevel(status == ResourceMonitorStatus.CRITICAL ? SystemEvent.EventLevel.CRITICAL : SystemEvent.EventLevel.INFO)
                .withSubsystemUuid(subsystemUuid)
                .withMessage(message)
                .withTtl(ttl)
                .withParams(params)
                .build());
    }
}