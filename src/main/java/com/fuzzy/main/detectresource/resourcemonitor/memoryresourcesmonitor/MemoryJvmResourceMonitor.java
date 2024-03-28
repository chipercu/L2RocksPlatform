package com.fuzzy.main.detectresource.resourcemonitor.memoryresourcesmonitor;

import com.fuzzy.main.detectresource.PlatformEventType;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.memorysensor.JvmMemorySensor;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.memorysensor.MemorySensor;
import com.fuzzy.main.platform.exception.PlatformException;

public class MemoryJvmResourceMonitor extends MemoryResourceMonitor {
    private final MemorySensor sensor;
    public static final PlatformEventType eventType = PlatformEventType.MEMORY_JVM_MONITORING;

    public MemoryJvmResourceMonitor(ResourceMonitorBuilder builder) {
        super(builder);
        this.sensor = new JvmMemorySensor();
    }

    @Override
    public ResourceMonitorContext scan() throws PlatformException {
        return apply(eventType);
    }

    @Override
    protected Double scanMemoryActivity() {
        return Math.round(sensor.getUsedMemory() * 1E4 / sensor.getTotalMemory()) / 1E2;
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
                Math.round(sensor.getTotalMemory() / 1048576. * 1E2) / 1E2 +
                " MB, used: " +
                Math.round(sensor.getUsedMemory() / 1048576. * 1E2) / 1E2 +
                " MB, freely: " +
                Math.round(sensor.getFreeMemory() / 1048576. * 1E2) / 1E2 +
                " MB";
    }
}