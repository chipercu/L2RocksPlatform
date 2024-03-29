package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.memorysensor;

public interface MemorySensor {
    Long getFreeMemory();
    Long getTotalMemory();
    Long getUsedMemory();
}