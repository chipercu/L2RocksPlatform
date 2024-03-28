package com.fuzzy.main.detectresource.resourcemonitor.sensor.cpusensor;

public interface CpuSensor {
    Double scanCPUActivity() throws InterruptedException;
    Integer getPhysicalProcessorCount();
}