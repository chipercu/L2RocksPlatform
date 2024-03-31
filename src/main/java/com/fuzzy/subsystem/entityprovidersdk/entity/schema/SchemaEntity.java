package com.fuzzy.subsystem.entityprovidersdk.entity.schema;

import com.fuzzy.subsystem.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.subsystem.entityprovidersdk.entity.DataContainer;
import com.fuzzy.subsystem.entityprovidersdk.entity.datasource.DataSourceProvider;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;

import java.util.Map;
import java.util.Objects;

public class SchemaEntity<T extends DataContainer> {

    private final Class<T> container;
    private final String name;
    private final String prefix;
    private final Map<EntityFieldInfo, com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField> fields;
    private final DataSourceProvider<T> dataSourceProvider;

    private SchemaEntity(Builder<T> builder) {
        container = builder.container;
        name = builder.name;
        prefix = builder.prefix;
        fields = builder.fields;
        dataSourceProvider = builder.dataSourceProvider;
    }

    public static <T extends DataContainer> Builder<T> newBuilder() {
        return new Builder<>();
    }


    public Class<T> getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<EntityFieldInfo, com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField> getFields() {
        return fields;
    }

    public DataSourceProvider<T> getDataSourceProvider() {
        return dataSourceProvider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchemaEntity<?> that = (SchemaEntity<?>) o;
        return Objects.equals(container, that.container) && Objects.equals(name, that.name) && Objects.equals(prefix, that.prefix) && Objects.equals(fields, that.fields) && Objects.equals(dataSourceProvider, that.dataSourceProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(container, name, prefix, fields, dataSourceProvider);
    }


    @Override
    public String toString() {
        return "EntitySchema{" +
                "container=" + container +
                ", name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", fields=" + fields +
                ", dataSourceProvider=" + dataSourceProvider +
                '}';
    }

    public static final class Builder<T extends DataContainer> {
        private Class<T> container;
        private String name;
        private String prefix;
        private Map<EntityFieldInfo, com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField> fields;
        private DataSourceProvider<T> dataSourceProvider;

        private Builder() {
        }

        public Builder<T> withContainer(Class<T> container) {
            this.container = container;
            return this;
        }

        public Builder<T> withName(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder<T> withFields(Map<EntityFieldInfo, SchemaField> fields) {
            this.fields = fields;
            return this;
        }

        public Builder<T> withDataSourceProvider(DataSourceProvider<T> dataSourceProvider) {
            this.dataSourceProvider = dataSourceProvider;
            return this;
        }

        public SchemaEntity<T> build() {
            return new SchemaEntity<>(this);
        }
    }
}
