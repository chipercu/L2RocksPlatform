package com.fuzzy.platform.service.detectresource.resourcemonitor.diskresourcesmonitor;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.disksensor.DiskSpaceSensor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.disksensor.HostDiskSensor;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

//TODO учесть возможность анализа при бекапировании базы данных
public class DiskHostResourceMonitor extends DiskResourceMonitor {
    public static final PlatformEventType eventType = PlatformEventType.DISK_HOST_MONITORING;
    private final DiskSpaceSensor sensor;
    private final Path directory;

    public DiskHostResourceMonitor(Path directory, ResourceMonitorBuilder builder) {
        super(builder);
        this.directory = directory;
        sensor = new HostDiskSensor(directory);
    }

    @Override
    public ResourceMonitorContext scan() throws PlatformException {
        return apply(eventType);
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
    protected Double scanDiskActivity() throws IOException {
        return Math.round(sensor.getFreeSpace() / 1073741824D * 100) / 100D;
    }

    @Override
    protected String updateMessage() throws IOException {
        return eventType.name().concat(":") +
                " name: " +
                directory.toString() +
                ", total: " +
                Math.round(sensor.getTotalSpace() / 1048576D * 100) / 100 +
                " MB, used: " +
                Math.round(sensor.getUsedSpace() / 1048576D * 100) / 100 +
                " MB, free: " +
                Math.round(sensor.getFreeSpace() / 1048576D * 100) / 100 +
                " MB";
    }

    @Override
    protected HashMap<String, Serializable> getParams() throws IOException {
        return new HashMap<>(Map.of(eventType.name(), getDiskUsage()));
    }

    private double getDiskUsage() throws IOException {
        return sensor.getUsedSpace() / 1048576D;
    }
}