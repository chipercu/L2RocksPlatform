package com.fuzzy.platform.service.detectresource.resourcemonitor.diskresourcesmonitor;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.function.Predicate;

public abstract class DiskResourceMonitor implements ResourceMonitor {
    protected final Duration ttl;
    protected final Duration period;
    protected final Predicate<Number> condition;
    protected final String uuid;

    protected DiskResourceMonitor(ResourceMonitorBuilder builder) {
        this.ttl = builder.ttl;
        this.period = builder.period;
        this.condition = builder.condition;
        this.uuid = builder.uuid;
    }

    protected ResourceMonitorContext apply(PlatformEventType eventType) throws PlatformException {
        ResourceMonitorContext.Builder builder = ResourceMonitorContext.newBuilder()
                .withPeriod(period)
                .withEventType(eventType)
                .withTtl(ttl)
                .withUUID(uuid);
        try {
            return builder
                    .withParams(getParams())
                    .withMessage(updateMessage())
                    .withStatus(getStatus())
                    .build();
        } catch (IOException e) {
            throw CoreExceptionBuilder.buildMonitorMeasurementException(e);
        }
    }

    private ResourceMonitorStatus getStatus() throws IOException {
        return condition.test(scanDiskActivity()) ? ResourceMonitorStatus.CRITICAL : ResourceMonitorStatus.NORMAL;
    }

    protected abstract Double scanDiskActivity() throws IOException;

    protected abstract String updateMessage() throws IOException;

    protected abstract HashMap<String, Serializable> getParams() throws IOException;
}