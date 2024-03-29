package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.memorysensor;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class JvmMemorySensor implements MemorySensor{
    public final OperatingSystemMXBean operatingSystemMXBean;

    public JvmMemorySensor() {
        operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public Long getFreeMemory() {
        return operatingSystemMXBean.getFreePhysicalMemorySize();
    }
    @Override
    public Long getTotalMemory() {
        return operatingSystemMXBean.getTotalPhysicalMemorySize();
    }
    @Override
    public Long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }
}