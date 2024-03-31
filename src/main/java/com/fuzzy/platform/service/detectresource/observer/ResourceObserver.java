package com.fuzzy.platform.service.detectresource.observer;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;

public interface ResourceObserver {
    void execute(String eventType,
                 String subsystemUuid,
                 String message,
                 Duration ttl,
                 ResourceMonitorStatus status,
                 HashMap<String, Serializable> params) throws PlatformException;
}