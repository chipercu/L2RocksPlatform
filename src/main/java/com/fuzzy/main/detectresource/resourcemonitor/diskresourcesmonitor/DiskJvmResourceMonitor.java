package com.fuzzy.main.detectresource.resourcemonitor.diskresourcesmonitor;

import com.fuzzy.main.detectresource.PlatformEventType;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.disksensor.DiskSpaceSensor;
import com.fuzzy.main.detectresource.resourcemonitor.sensor.disksensor.DockerDiskSensor;
import com.fuzzy.main.platform.exception.PlatformException;

import java.io.IOException;
import java.nio.file.Path;

//TODO учесть возможность анализа при бекапировании базы данных
public class DiskJvmResourceMonitor extends DiskResourceMonitor {
    public static final PlatformEventType eventType = PlatformEventType.DISK_JVM_MONITORING;
    private final DiskSpaceSensor sensor;
    private final Path directory;

    public DiskJvmResourceMonitor(Path directory, ResourceMonitorBuilder builder) {
        super(builder);
        sensor = new DockerDiskSensor(directory);
        this.directory = directory;
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
        return Math.round(sensor.getFreeSpace() / 1073741824D * 1E2) / 1E2;
    }

    @Override
    protected String updateMessage() throws IOException {
        return eventType.name().concat(":") +
                " name: " +
                directory.toString() +
                ", total: " +
                Math.round(sensor.getTotalSpace() / 1048576D * 1E2) / 1E2 +
                " MB, used: " +
                Math.round(sensor.getUsedSpace() / 1048576D * 1E2) / 1E2 +
                " MB, free: " +
                Math.round(sensor.getFreeSpace() / 1048576D * 1E2) / 1E2 +
                " MB";
    }
}