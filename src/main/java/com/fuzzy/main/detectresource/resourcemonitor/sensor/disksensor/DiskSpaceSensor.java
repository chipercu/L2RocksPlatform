package com.fuzzy.main.detectresource.resourcemonitor.sensor.disksensor;

import java.io.IOException;

public interface DiskSpaceSensor {
    Long getTotalSpace() throws IOException;
    Long getFreeSpace() throws IOException;
    Long getUsedSpace() throws IOException;
}