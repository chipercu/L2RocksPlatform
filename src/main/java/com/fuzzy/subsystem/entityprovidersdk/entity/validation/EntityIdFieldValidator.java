package com.fuzzy.subsystem.entityprovidersdk.entity.validation;

import com.fuzzy.subsystem.entityprovidersdk.entity.EntityField;
import com.fuzzy.subsystem.entityprovidersdk.entity.Id;
import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.subsystem.entityprovidersdk.entity.validation.Validator;
import com.fuzzy.subsystem.entityprovidersdk.exception.runtime.SchemeValidationException;

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
