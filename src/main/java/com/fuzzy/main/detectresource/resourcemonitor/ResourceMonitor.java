package com.fuzzy.main.detectresource.resourcemonitor;

import com.fuzzy.main.platform.exception.PlatformException;

public interface ResourceMonitor {
    ResourceMonitorContext scan() throws PlatformException;
    ResourceMonitorContext getParameters();
}
