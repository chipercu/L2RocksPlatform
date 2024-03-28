package com.fuzzy.main.detectresource.resourcemonitor.sensor.disksensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

public class HostDiskSensor implements DiskSpaceSensor{
    private final FileSystem fileSystem;
    private Optional<OSFileStore> fileStore = Optional.empty();
    private final Logger log = LoggerFactory.getLogger(HostDiskSensor.class);
    private final Path directory;
    private Predicate<OSFileStore> storeFilter = o -> false;

    public HostDiskSensor(Path directory) {
        this.directory = directory;
        fileSystem = new SystemInfo().getOperatingSystem()
                .getFileSystem();
        if(Files.exists(directory)) {
            storeFilter = osFileStore -> directory.startsWith(osFileStore.getMount());
        }
        updateFileStore();
    }

    @Override
    public Long getTotalSpace() {
        updateFileStore();
        return fileStore.map(OSFileStore::getTotalSpace)
                .orElse(0L);
    }

    @Override
    public Long getFreeSpace() {
        updateFileStore();
        return fileStore.map(OSFileStore::getFreeSpace)
                .orElse(0L);
    }

    @Override
    public Long getUsedSpace() {
        updateFileStore();
        return fileStore.map(fileStores -> fileStores.getTotalSpace() - fileStores.getFreeSpace())
                .orElse(0L);
    }

    private void updateFileStore() {
        fileStore = fileSystem.getFileStores().stream()
                .filter(storeFilter)
                .findFirst();
        if(!fileStore.isPresent()) {
            log.info("HostMemorySensor: path {} doesn't found in file system", directory);
        }
    }
}