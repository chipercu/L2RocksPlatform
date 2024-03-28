package com.fuzzy.main.detectresource.resourcemonitor;

import com.fuzzy.main.detectresource.PlatformEventType;

import java.time.Duration;

public class ResourceMonitorContext {
    private final ResourceMonitorStatus event;
    private final Duration ttl;
    private final Duration period;
    private final String uuid;
    private final PlatformEventType eventType;
    private final String message;

    private ResourceMonitorContext(Builder builder) {
        this.event = builder.status;
        this.ttl = builder.ttl;
        this.period = builder.period;
        this.uuid = builder.uuid;
        this.eventType = builder.eventType;
        this.message = builder.message;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ResourceMonitorStatus getEvent() {
        return event;
    }

    public Duration getTtl() {
        return ttl;
    }

    public Duration getPeriod() {
        return period;
    }

    public String getUuid() {
        return uuid;
    }

    public PlatformEventType getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public static final class Builder {
        public String message;
        private ResourceMonitorStatus status;
        private Duration ttl;
        private Duration period;
        private String uuid;
        private PlatformEventType eventType;

        private Builder() {
        }

        public Builder withStatus(ResourceMonitorStatus context) {
            this.status = context;
            return this;
        }

        public Builder withTtl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder withPeriod(Duration period) {
            this.period = period;
            return this;
        }

        public Builder withUUID(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder withEventType(PlatformEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public ResourceMonitorContext build() {
            return new ResourceMonitorContext(this);
        }
    }
}