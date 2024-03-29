package com.fuzzy.main.entityprovidersdk.entity.validation;

import com.fuzzy.main.entityprovidersdk.entity.EntityField;
import com.fuzzy.main.entityprovidersdk.entity.Id;
import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.main.entityprovidersdk.exception.runtime.SchemeValidationException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EntityIdFieldValidator implements Validator<Collection<SchemaField>> {
    @Override
    public boolean validate(Collection<SchemaField> schemaFields) {

        final List<Method> idMethods = schemaFields.stream()
                .map(SchemaField::getDomainObjectMethod)
                .filter(method -> Objects.nonNull(method.getAnnotation(Id.class))
                        && Objects.nonNull(method.getAnnotation(EntityField.class))
                        && (method.getReturnType().equals(long.class) ||
                        method.getReturnType().equals(Long.class)))
                .toList();
        if (idMethods.size() != 1) {
            final String clazz = schemaFields.stream().findFirst().map(fieldSchema -> fieldSchema.getContainer().getName()).orElse("");
            throw new SchemeValidationException("Entity must have single column with annotation @Id. Check your container: %s".formatted(clazz));
        }
        return true;
    }
}
