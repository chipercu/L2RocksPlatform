package com.fuzzy.subsystem.entityprovidersdk.data;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystem.entityprovidersdk.enums.DataType;

import java.util.Objects;

public class EntityFieldInfo implements RemoteObject {

    private final String name;
    private final DataType type;

    private EntityFieldInfo(Builder builder) {
        name = builder.name;
        type = builder.type;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityFieldInfo that = (EntityFieldInfo) o;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "EntityFieldInfo{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    public static final class Builder {
        private String name;
        private DataType type;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(DataType type) {
            this.type = type;
            return this;
        }

        public EntityFieldInfo build() {
            return new EntityFieldInfo(this);
        }
    }
}
