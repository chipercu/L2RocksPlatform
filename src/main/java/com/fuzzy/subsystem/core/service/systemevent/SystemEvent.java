package com.fuzzy.subsystem.core.service.systemevent;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SystemEvent implements RemoteObject {

    private static final String EVENT_TYPE_FIELD = "eventType";
    private static final String LEVEL_FIELD = "level";
    private static final String SUBSYSTEM_UUID_FIELD = "subsystemUuid";
    private static final String MESSAGE_FIELD = "message";
    private static final String TTL_FIELD = "ttl";

    private final String eventType;
    private final ZonedDateTime time;
    private final EventLevel level;
    private final String subsystemUuid;
    private final String message;
    private final Duration ttl;
    private final HashMap<String, Serializable> params;

    private SystemEvent(Builder builder) {
        eventType = builder.eventType;
        time = builder.time;
        level = builder.level;
        subsystemUuid = builder.subsystemUuid;
        message = builder.message;
        ttl = builder.ttl;
        params = builder.params;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getEventType() {
        return eventType;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public EventLevel getLevel() {
        return level;
    }

    public String getSubsystemUuid() {
        return subsystemUuid;
    }

    public String getMessage() {
        return message;
    }

    public Duration getTtl() {
        return ttl;
    }

    public HashMap<String, Serializable> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemEvent systemEvent1 = (SystemEvent) o;
        return Objects.equals(eventType, systemEvent1.eventType)
                && Objects.equals(time, systemEvent1.time)
                && level == systemEvent1.level
                && Objects.equals(subsystemUuid, systemEvent1.subsystemUuid)
                && Objects.equals(message, systemEvent1.message)
                && Objects.equals(ttl, systemEvent1.ttl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, time, level, subsystemUuid, message, ttl);
    }

    public static final class Builder {
        private String eventType;
        private ZonedDateTime time;
        private EventLevel level;
        private String subsystemUuid;
        private String message;
        private Duration ttl;
        private HashMap<String, Serializable> params;

        private Builder() {
            params = new HashMap<>();
        }

        public Builder withEventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder withLevel(EventLevel level) {
            this.level = level;
            return this;
        }

        public Builder withSubsystemUuid(String subsystemUuid) {
            this.subsystemUuid = subsystemUuid;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withTtl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder withParams(HashMap<String, Serializable> params) {
            if (params != null) {
                this.params = params;
            }
            return this;
        }

        public SystemEvent build() throws PlatformException {
            this.time = ZonedDateTime.now();
            validate();
            return new SystemEvent(this);
        }

        private void validate() throws PlatformException {
            if (Objects.isNull(this.eventType)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(EVENT_TYPE_FIELD);
            }
            if (Objects.isNull(this.level)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(LEVEL_FIELD);
            }

            if (StringUtils.isEmpty(this.subsystemUuid)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(SUBSYSTEM_UUID_FIELD);
            }

            if (StringUtils.isEmpty(this.message)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(MESSAGE_FIELD);
            }

            if (Objects.isNull(this.ttl)) {
                throw GeneralExceptionBuilder.buildEmptyValueException(TTL_FIELD);
            }
        }
    }

    public enum EventLevel {
        CRITICAL(1),
        ERROR(2),
        WARNING(3),
        INFO(4);

        private final int level;

        EventLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public EventLevel get(int level) {
            for (EventLevel value : EventLevel.values()) {
                if (value.level == level) {
                    return value;
                }
            }
            throw new NoSuchElementException();
        }
    }
}