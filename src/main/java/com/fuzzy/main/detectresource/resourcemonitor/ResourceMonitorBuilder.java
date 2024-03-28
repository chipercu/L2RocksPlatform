package com.fuzzy.main.detectresource.resourcemonitor;

import java.time.Duration;
import java.util.function.Predicate;

public class ResourceMonitorBuilder {
    public Predicate<Number> condition;
    public Duration period;
    public Duration ttl;
    public String uuid;

    public static ResourceMonitorBuilder newBuilder() {
        return new ResourceMonitorBuilder();
    }

    public ResourceMonitorBuilder withCondition(Predicate<Number> condition) {
        this.condition = condition;
        return this;
    }

    public ResourceMonitorBuilder withPeriod(Duration period) {
        this.period = period;
        return this;
    }

    public ResourceMonitorBuilder withTtl(Duration ttl) {
        this.ttl = ttl;
        return this;
    }

    public ResourceMonitorBuilder withUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }


}