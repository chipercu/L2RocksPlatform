package com.fuzzy.platform.service.detectresource.resourcemonitor.cpuresourcesmonitor;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor.CpuSensor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor.JvmCpuSensor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CpuJvmResourcesMonitor extends CpuResourceMonitor {
    private final CpuSensor sensor;
    public static final PlatformEventType eventType = PlatformEventType.CPU_JVM_MONITORING;

    public CpuJvmResourcesMonitor(ResourceMonitorBuilder builder) {
        super(builder);
        this.sensor = new JvmCpuSensor();
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