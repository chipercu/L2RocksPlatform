package com.fuzzy.platform.service.detectresource.resourcemonitor.cpuresourcesmonitor;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.platform.service.detectresource.PlatformEventType;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitor;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorBuilder;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorContext;
import com.fuzzy.platform.service.detectresource.resourcemonitor.ResourceMonitorStatus;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class CpuResourceMonitor implements ResourceMonitor {
    private static final Integer measurementResolution = 10;
    protected final Duration ttl;
    protected final Duration period;
    protected final Predicate<Number> condition;
    protected final String uuid;
    private final Queue<Double> measurementPoints = new ArrayDeque<>(measurementResolution);


    protected CpuResourceMonitor(ResourceMonitorBuilder builder) {
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
            updateMeasurementPoints(scanCPUActivity());
            return builder
                    .withMessage(updateMessage())
                    .withStatus(getStatus())
                    .build();
        } catch (InterruptedException e) {
            throw CoreExceptionBuilder.buildMonitorMeasurementException(e);
        }
    }

    private ResourceMonitorStatus getStatus() {
        return condition.test(calcAverageValue()) ? ResourceMonitorStatus.CRITICAL : ResourceMonitorStatus.NORMAL;
    }

    protected abstract Double scanCPUActivity() throws InterruptedException;

    private void updateMeasurementPoints(Double newPoint) {
        if (measurementPoints.size() == measurementResolution) {
            measurementPoints.poll();
        }
        if (Objects.nonNull(newPoint)) {
            measurementPoints.add(newPoint);
        }
    }

    protected Double calcAverageValue() {
        return measurementPoints.stream()
                .collect(Collectors.summarizingDouble(Double::doubleValue))
                .getAverage();
    }

    protected abstract String updateMessage() throws InterruptedException;

    protected abstract HashMap<String, Serializable> getParams();
}