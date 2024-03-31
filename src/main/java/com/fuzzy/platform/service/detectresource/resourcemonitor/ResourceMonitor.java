package com.fuzzy.platform.service.detectresource.resourcemonitor;

import com.fuzzy.platform.exception.PlatformException;

public interface ResourceMonitor {
    ResourceMonitorContext scan() throws PlatformException;
    ResourceMonitorContext getParameters();
}
