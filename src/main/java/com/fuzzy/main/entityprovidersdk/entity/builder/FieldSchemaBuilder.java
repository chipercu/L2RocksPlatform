package com.fuzzy.main.entityprovidersdk.entity.builder;

import com.fuzzy.main.entityprovidersdk.data.EntityFieldInfo;
import com.fuzzy.main.entityprovidersdk.entity.DataContainer;
import com.fuzzy.main.entityprovidersdk.entity.EntityField;
import com.fuzzy.main.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.main.entityprovidersdk.entity.validation.EntityIdFieldValidator;
import com.fuzzy.main.entityprovidersdk.entity.validation.FieldReturnTypeValidator;
import com.fuzzy.main.entityprovidersdk.entity.validation.FieldUniqueValidator;
import com.fuzzy.main.entityprovidersdk.exception.runtime.SchemeValidationException;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FieldSchemaBuilder {

    private final EntityIdFieldValidator idFieldValidator;
    private final FieldUniqueValidator fieldUniqueValidator;
    private final FieldReturnTypeValidator returnTypeValidator;

    public FieldSchemaBuilder() {
        idFieldValidator = new EntityIdFieldValidator();
        fieldUniqueValidator = new FieldUniqueValidator();
        returnTypeValidator = new FieldReturnTypeValidator();
    }

    public <T extends DataContainer> Map<EntityFieldInfo, SchemaField> createFields(@NonNull Class<T> container) {

        final List<SchemaField> schemaFields = Arrays.stream(container.getMethods())
                .filter(method -> Objects.nonNull(method.getAnnotation(EntityField.class)))
                .filter(method -> (method.getModifiers() & Modifier.PUBLIC) != 0)
                .filter(method -> Predicate.isEqual(0).test(method.getParameterCount()))
                .map(method -> create(method, container))
                .filter(returnTypeValidator::validate)
                .toList();

        if (Objects.isNull(schemaFields) || schemaFields.isEmpty()) {
            throw new SchemeValidationException("class %s description must have at least one field".formatted(container));
        }
        fieldUniqueValidator.validate(schemaFields);
        idFieldValidator.validate(schemaFields);


        return schemaFields
                .stream()
                .collect(Collectors.toMap(fieldSchema ->
                                EntityFieldInfo.newBuilder()
                                        .withName(fieldSchema.getName())
                                        .withType(fieldSchema.getType())
                                        .build()
                        , Function.identity()));
    }

    private SchemaField create(Method method, Class<?> container) {
        EntityField declaredAnnotation = method.getDeclaredAnnotation(EntityField.class);
        method.setAccessible(true);
        return SchemaField.newBuilder()
                .withName(declaredAnnotation.name())
                .withType(declaredAnnotation.type())
                .withDomainObjectMethod(method)
                .withContainer(container)
                .isNullable(processNullable(method))
                .build();
    }


    private boolean processNullable(Method method) {
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof Class<?> clazz) {
            return !int.class.equals(clazz) && !boolean.class.equals(clazz) && !long.class.equals(clazz) && !double.class.equals(clazz);
        }
        return true;
    }

}
