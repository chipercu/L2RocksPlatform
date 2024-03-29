package com.fuzzy.main.entityprovidersdk.entity.schema;

import com.fuzzy.main.entityprovidersdk.entity.DataContainer;
import com.fuzzy.main.entityprovidersdk.enums.DataType;

import java.lang.reflect.Method;
import java.util.Objects;

public class SchemaField {

    private Class<?> container;
    private String name;
    private DataType type;
    private Method domainObjectMethod;
    boolean isNullable;

    private SchemaField(Builder builder) {
        container = builder.container;
        name = builder.name;
        type = builder.type;
        domainObjectMethod = builder.domainObjectMethod;
        isNullable = builder.isNullable;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Class<?> getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public Method getDomainObjectMethod() {
        return domainObjectMethod;
    }

    public boolean isNullable() {
        return isNullable;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchemaField that = (SchemaField) o;

        if (isNullable != that.isNullable) return false;
        if (!Objects.equals(container, that.container)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (type != that.type) return false;
        return Objects.equals(domainObjectMethod, that.domainObjectMethod);
    }

    @Override
    public int hashCode() {
        int result = container != null ? container.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (domainObjectMethod != null ? domainObjectMethod.hashCode() : 0);
        result = 31 * result + (isNullable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SchemaField{" +
                "container=" + container +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", domainObjectMethod=" + domainObjectMethod +
                ", isNullable=" + isNullable +
                '}';
    }

    public static final class Builder<T extends DataContainer> {

        private Class<T> container;
        private String name;
        private DataType type;
        private Method domainObjectMethod;
        boolean isNullable;

        private Builder() {
        }

        public Builder withContainer(Class<T> container) {
            this.container = container;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(DataType type) {
            this.type = type;
            return this;
        }

        public Builder withDomainObjectMethod(Method domainObjectMethod) {
            this.domainObjectMethod = domainObjectMethod;
            return this;
        }

        public Builder isNullable(boolean isNullable) {
            this.isNullable = isNullable;
            return this;
        }


        public SchemaField build() {
            return new SchemaField(this);
        }
    }
}
