package com.fuzzy.main.detectresource.resourcemonitor.sensor.memorysensor;

public interface MemorySensor {
    Long getFreeMemory();
    Long getTotalMemory();
    Long getUsedMemory();
}