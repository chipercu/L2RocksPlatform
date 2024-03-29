package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.memorysensor;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

public class HostMemorySensor implements MemorySensor {
    private final GlobalMemory memory;

    public HostMemorySensor() {
        memory = new SystemInfo().getHardware().getMemory();
    }
    @Override
    public Long getFreeMemory() {
        return memory.getAvailable();
    }
    @Override
    public Long getTotalMemory() {
        return memory.getTotal();
    }
    @Override
    public Long getUsedMemory() {
        return memory.getTotal() - memory.getAvailable();
    }
}