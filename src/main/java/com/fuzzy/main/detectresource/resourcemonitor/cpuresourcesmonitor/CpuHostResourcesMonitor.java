package com.fuzzy.main.detectresource.resourcemonitor.cpuresourcesmonitor;

import com.fuzzy.main.detectresource.PlatformEventType;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.cpusensor.CpuSensor;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.cpusensor.HostCpuSensor;
import com.fuzzy.main.platform.exception.PlatformException;

public class CpuHostResourcesMonitor extends CpuResourceMonitor {
    private final CpuSensor sensor;
    public static final PlatformEventType eventType = PlatformEventType.CPU_HOST_MONITORING;

    public CpuHostResourcesMonitor(ResourceMonitorBuilder builder) {
        super(builder);
        this.sensor = new HostCpuSensor();
    }

    @Override
    public ResourceMonitorContext scan() throws PlatformException {
        return apply(eventType);
    }

    @Override
    protected Double scanCPUActivity() throws InterruptedException {
        return sensor.scanCPUActivity();
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
    protected String updateMessage() throws InterruptedException {
        return eventType.name().concat(":") +
                " number of Cores: " +
                sensor.getPhysicalProcessorCount() +
                ", percentage of total load: " +
                Math.round(calcAverageValue() * 1E2) / 1E2;
    }
}