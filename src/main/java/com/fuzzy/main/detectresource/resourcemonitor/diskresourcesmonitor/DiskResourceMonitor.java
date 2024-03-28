package com.fuzzy.main.detectresource.resourcemonitor.diskresourcesmonitor;

import com.fuzzy.main.detectresource.PlatformEventType;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitor;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.main.detectresource.resourcemonitor.ResourceMonitorStatus;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.io.IOException;
import java.time.Duration;
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
            if (condition.test(scanDiskActivity())) {
                return builder.withMessage(updateMessage()).withStatus(ResourceMonitorStatus.CRITICAL).build();
            }
            return builder.withStatus(ResourceMonitorStatus.NORMAL).build();
        } catch (IOException e) {
            throw CoreExceptionBuilder.buildMonitorMeasurementException(e);
        }
    }

    protected abstract Double scanDiskActivity() throws IOException;

    protected abstract String updateMessage() throws IOException;
}