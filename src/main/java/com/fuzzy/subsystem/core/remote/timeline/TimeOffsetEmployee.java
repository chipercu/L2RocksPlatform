package com.fuzzy.subsystem.core.remote.timeline;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.util.Objects;

public class TimeOffsetEmployee implements RemoteObject {
    private final long employeeId;
    private final int offset;

    private TimeOffsetEmployee(Builder builder) {
        employeeId = builder.employeeId;
        offset = builder.offset;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeOffsetEmployee that = (TimeOffsetEmployee) o;
        return employeeId == that.employeeId && offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, offset);
    }

    @Override
    public String toString() {
        return "EmployeeTimezone{" +
                "employeeId=" + employeeId +
                ", offset=" + offset +
                '}';
    }

    public static final class Builder {
        private long employeeId;
        private int offset;

        private Builder() {
        }

        public Builder withEmployeeId(long employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder withOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public TimeOffsetEmployee build() {
            return new TimeOffsetEmployee(this);
        }
    }
}
