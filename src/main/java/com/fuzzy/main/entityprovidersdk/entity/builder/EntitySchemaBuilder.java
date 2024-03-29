package com.fuzzy.main.entityprovidersdk.entity.builder;

import com.fuzzy.main.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.main.entityprovidersdk.entity.DataContainer;
import com.fuzzy.main.entityprovidersdk.entity.EntityClass;
import com.fuzzy.main.entityprovidersdk.entity.datasource.DataSourceProvider;
import com.fuzzy.main.entityprovidersdk.entity.datasource.DomainObjectDataSource;
import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaEntity;
import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.main.entityprovidersdk.exception.runtime.SchemeValidationException;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;

public class EntitySchemaBuilder {


    private final FieldSchemaBuilder fieldSchemaBuilder;

    public EntitySchemaBuilder() {
        fieldSchemaBuilder = new FieldSchemaBuilder();
    }

    public <T extends DataContainer> SchemaEntity<T> createSchema(@NonNull Class<T> container) {

        EntityClass entityClassAnnotation = container.getDeclaredAnnotation(EntityClass.class);
        final Map<EntityFieldInfo, SchemaField> fields = fieldSchemaBuilder.createFields(container);
        if (fields.isEmpty()) {
            return null;
        }

        return SchemaEntity.<T>newBuilder()
                .withContainer(container)
                .withFields(fields)
                .withDataSourceProvider(createDataSource(container))
                .withPrefix(entityClassAnnotation.uuid())
                .withName(entityClassAnnotation.name())
                .build();
    }

    private <T extends DataContainer> DataSourceProvider<T> createDataSource(Class<T> container) {

        EntityClass entityClassAnnotation = container.getDeclaredAnnotation(EntityClass.class);
        final Class<? extends DataSourceProvider> dataSourceClass = entityClassAnnotation.dataSource();

        if (Objects.isNull(dataSourceClass) || dataSourceClass.isInterface()) {
            return new DomainObjectDataSource<>(container);
        } else {
            try {
                return dataSourceClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new SchemeValidationException("error during creation DataSourceProvider", e);
            }
        }
    }

}
