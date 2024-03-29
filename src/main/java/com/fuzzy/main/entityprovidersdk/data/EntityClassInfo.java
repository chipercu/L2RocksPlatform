package com.fuzzy.main.entityprovidersdk.data;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;

import java.util.Objects;

public class EntityClassInfo implements RemoteObject {
    private final String name;
    private final String prefix;

    private EntityClassInfo(Builder builder) {
        name = builder.name;
        prefix = builder.prefix;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityClassInfo that = (EntityClassInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, prefix);
    }

    @Override
    public String toString() {
        return "EntityClassInfo{" +
                "name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }

    public static final class Builder {
        private String name;
        private String prefix;

        private Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public EntityClassInfo build() {
            return new EntityClassInfo(this);
        }
    }
}
