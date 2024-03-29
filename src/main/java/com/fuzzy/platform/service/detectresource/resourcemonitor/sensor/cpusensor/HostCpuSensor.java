package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.cpusensor;


import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HostCpuSensor implements CpuSensor {
    private static final Integer intervalValue = 300;
    private final CentralProcessor processor;
    private final Duration measuringInterval;

    public HostCpuSensor() {
        processor = new SystemInfo().getHardware().getProcessor();
        measuringInterval = Duration.ofMillis(intervalValue);
    }

    @Override
    public Double scanCPUActivity() throws InterruptedException {
        long[] systemCpuLoadTicks = processor.getSystemCpuLoadTicks();
        TimeUnit.MILLISECONDS.sleep(measuringInterval.toMillis());
        return Math.max(0, processor.getSystemCpuLoadBetweenTicks(systemCpuLoadTicks)) * 100D;
    }

    @Override
    public Integer getPhysicalProcessorCount() {
        return processor.getPhysicalProcessorCount();
    }
}