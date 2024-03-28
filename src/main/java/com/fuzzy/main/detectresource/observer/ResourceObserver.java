package com.fuzzy.main.detectresource.observer;

import com.fuzzy.main.platform.exception.PlatformException;

import java.time.Duration;

public interface ResourceObserver {
    void execute(String eventType, String subsystemUuid, String message, Duration ttl) throws PlatformException;
}