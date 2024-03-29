package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor;

public interface CpuSensor {
    Double scanCPUActivity() throws InterruptedException;
    Integer getPhysicalProcessorCount();
}