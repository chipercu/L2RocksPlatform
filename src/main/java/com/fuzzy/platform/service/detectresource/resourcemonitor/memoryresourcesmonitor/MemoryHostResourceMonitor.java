package com.fuzzy.platform.service.detectresource.resourcemonitor.memoryresourcesmonitor;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.memorysensor.HostMemorySensor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.memorysensor.MemorySensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MemoryHostResourceMonitor extends MemoryResourceMonitor {
    private final MemorySensor sensor;
    public static final PlatformEventType eventType = PlatformEventType.MEMORY_HOST_MONITORING;

    public MemoryHostResourceMonitor(ResourceMonitorBuilder builder) {
        super(builder);
        this.sensor = new HostMemorySensor();
    }

    @Override
    public ResourceMonitorContext scan() throws PlatformException {
        return apply(eventType);
    }

    @Override
    protected Double scanMemoryActivity() {
        return ((sensor.getUsedMemory() / 1048576D) / (sensor.getTotalMemory() / 1048576D)) * 100;
    }

    @Override
    public ResourceMonitorContext getParameters() {
        return ResourceMonitorContext.newBuilder()
                .withTtl(ttl)
                .withPeriod(period)
                .withUUID(uuid)
                .withEventType(eventType)
                .build();
    }

    @Override
    protected String createMessage() {
        return eventType.name().concat(":") +
                " total: " +
                Math.round(sensor.getTotalMemory() / 1048576. * 100) / 100 +
                " MB, used: " +
                Math.round(sensor.getUsedMemory() / 1048576. * 100) / 100 +
                " MB, freely: " +
                Math.round(sensor.getFreeMemory() / 1048576. * 100) / 100 +
                " MB";
    }

    @Override
    protected HashMap<String, Serializable> getParams() {
        return new HashMap<>(Map.of(eventType.name(), getMemoryUsage()));
    }

    private double getMemoryUsage() {
        return sensor.getUsedMemory() / 1048576.;
    }
}