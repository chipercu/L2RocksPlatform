package com.fuzzy.platform.service.detectresource.observer;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResourceObservable {

    protected final List<ResourceObserver> observers = new CopyOnWriteArrayList<>();

    public ResourceObservable(ResourceObserver... observers) {
        if (observers != null && observers.length > 0) {
            this.observers.addAll(Arrays.asList(observers));
        }
    }

    public void addObserver(ResourceObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(ResourceObserver observer) {
        this.observers.remove(observer);
    }


    public void notifyObservers(String eventType,
                                String subsystemUuid,
                                String message, Duration ttl,
                                ResourceMonitorStatus status,
                                HashMap<String, Serializable> params) throws PlatformException {
        for (ResourceObserver observer : observers) {
            observer.execute(eventType, subsystemUuid, message, ttl, status, params);
        }
    }
}