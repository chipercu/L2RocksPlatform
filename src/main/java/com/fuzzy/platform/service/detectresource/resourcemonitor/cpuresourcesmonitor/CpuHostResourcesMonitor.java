package com.fuzzy.platform.service.detectresource.resourcemonitor.cpuresourcesmonitor;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor.CpuSensor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor.HostCpuSensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    protected String updateMessage() {
        return eventType.name().concat(":") +
                " number of Cores: " +
                sensor.getPhysicalProcessorCount() +
                ", percentage of total load: " +
                Math.round(calcAverageValue() * 100) / 100;
    }

    @Override
    protected HashMap<String, Serializable> getParams() {
        return new HashMap<>(Map.of(eventType.name(), calcAverageValue()));
    }
}