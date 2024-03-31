package com.fuzzy.subsystem.entityprovidersdk.entity.validation;

import com.fuzzy.subsystem.entityprovidersdk.entity.schema.SchemaField;
import com.fuzzy.subsystem.entityprovidersdk.entity.validation.Validator;
import com.fuzzy.subsystem.entityprovidersdk.enums.DataType;
import com.fuzzy.subsystem.entityprovidersdk.exception.runtime.SchemeValidationException;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;

public class FieldReturnTypeValidator implements Validator<SchemaField> {

    @Override
    public boolean validate(SchemaField source) {
        if (!validateAnnotateAndReturnType(source)) {
            final Method domainObjectMethod = source.getDomainObjectMethod();
            Type returnType = domainObjectMethod.getGenericReturnType();
            String errorMessage = String.format("Field validation error [ method:%s return type: %s] not supported [annotation type:%s]",
                    domainObjectMethod.getName(),
                    returnType.getTypeName(),
                    source.getType().name());
            throw new SchemeValidationException(errorMessage);
        }
        return true;
    }

    private static boolean validateAnnotateAndReturnType(SchemaField source) {
        Method method = source.getDomainObjectMethod();
        Type returnType = method.getGenericReturnType();
        final DataType dataType = source.getType();

        if (returnType instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
            if (typeArguments.length == 1 && typeArguments[0] instanceof Class<?> elementType) {
                if (String.class.equals(elementType)) {
                    return dataType == DataType.STRING_ARRAY;
                } else if (Integer.class.equals(elementType) || int.class.equals(elementType) || Short.class.equals(elementType)) {
                    return dataType == DataType.INTEGER_ARRAY;
                } else if (Boolean.class.equals(elementType) || boolean.class.equals(elementType)) {
                    return dataType == DataType.BOOLEAN_ARRAY;
                } else if (Long.class.equals(elementType) || long.class.equals(elementType)) {
                    return dataType == DataType.LONG_ARRAY;
                } else if (Double.class.equals(elementType) || double.class.equals(elementType)) {
                    return dataType == DataType.DOUBLE_ARRAY;
                } else if (Instant.class.equals(elementType)) {
                    return dataType == DataType.INSTANT_ARRAY;
                }
            }
        } else if (returnType instanceof Class<?> clazz) {
            if (String.class.equals(clazz)) {
                return dataType == DataType.STRING;
            } else if (Integer.class.equals(clazz) || int.class.equals(clazz) || Short.class.equals(clazz)) {
                return dataType == DataType.INTEGER;
            } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
                return dataType == DataType.BOOLEAN;
            } else if (Long.class.equals(clazz) || long.class.equals(clazz)) {
                return dataType == DataType.LONG;
            } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
                return dataType == DataType.DOUBLE;
            } else if (Instant.class.equals(clazz)) {
                return dataType == DataType.INSTANT;
            } else if (byte[].class.equals(clazz)) {
                return dataType == DataType.BYTE_ARRAY;
            }
        }

        throw new SchemeValidationException("type not supported %s".formatted(returnType));
    }
}
