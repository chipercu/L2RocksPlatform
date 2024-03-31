package com.fuzzy.platform.service.detectresource.resourcemonitor.memoryresourcesmonitor;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.function.Predicate;

public abstract class MemoryResourceMonitor implements ResourceMonitor {
    protected final Duration ttl;
    protected final Duration period;
    protected final Predicate<Number> condition;
    protected final String uuid;

    protected MemoryResourceMonitor(ResourceMonitorBuilder builder) {
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
                .withUUID(uuid)
                .withParams(getParams());
        try {
            return builder
                    .withMessage(createMessage())
                    .withStatus(getStatus())
                    .build();
        } catch (InterruptedException e) {
            throw CoreExceptionBuilder.buildMonitorMeasurementException(e);
        }
    }

    private ResourceMonitorStatus getStatus() throws InterruptedException {
        return condition.test(scanMemoryActivity()) ? ResourceMonitorStatus.CRITICAL : ResourceMonitorStatus.NORMAL;
    }

    protected abstract Double scanMemoryActivity() throws InterruptedException;

    protected abstract String createMessage();

    protected abstract HashMap<String, Serializable> getParams();
}