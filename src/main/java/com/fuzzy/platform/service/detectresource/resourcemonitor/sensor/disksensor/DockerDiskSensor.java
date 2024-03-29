package com.fuzzy.platform.service.detectresource.resourcemonitor.sensor.disksensor;

import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class DockerDiskSensor implements DiskSpaceSensor{
    private final Path directory;
    private final FileSystem fileSystem;
    private Optional<FileStore> fileStore;
    private final Logger log = LoggerFactory.getLogger(DockerDiskSensor.class);
    private Predicate<FileStore> storeFilter = o -> false;

    public DockerDiskSensor(Path directory) {
        this.directory = directory;
        fileSystem = directory.getFileSystem();
        if(Files.exists(directory)) {
            storeFilter = fs -> directory.startsWith(fs.toString().replaceFirst("\\s\\(.+", ""));
        }
        updateFileStore();
    }

    @Override
    public Long getTotalSpace() throws IOException {
        updateFileStore();
        if(fileStore.isPresent()) {
            return fileStore.get().getTotalSpace();
        }
        return 0L;
    }

    @Override
    public Long getFreeSpace() throws IOException {
        updateFileStore();
        if(fileStore.isPresent()) {
            return fileStore.get().getUsableSpace();
        }
        return 0L;
    }

    @Override
    public Long getUsedSpace() throws IOException {
        updateFileStore();
        if(fileStore.isPresent()) {
            return fileStore.get().getTotalSpace() - fileStore.get().getUsableSpace();
        }
        return 0L;
    }

    private void updateFileStore() {
        fileStore = Streams.stream(fileSystem.getFileStores())
                .filter(storeFilter)
                .reduce(BinaryOperator.maxBy(Comparator.comparing(FileStore::toString)));
        if(!fileStore.isPresent()) {
            log.info("DockerMemorySensor: path {} doesn't found in file system", directory);
        }
    }
}