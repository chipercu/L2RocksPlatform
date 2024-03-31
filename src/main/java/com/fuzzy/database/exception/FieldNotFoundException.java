package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class FieldNotFoundException extends SchemaException {

    public FieldNotFoundException(String fieldName, String tableName) {
        super("Field name=" + fieldName + " not found into '" + tableName + "'");
    }

    public FieldNotFoundException(int fieldId, String tableName) {
        super("Field id=" + fieldId + " not found into '" + tableName + "'");
    }

    public FieldNotFoundException(Class<?> clazz, String fieldName) {
        super("Field " + fieldName + " not found in " + clazz);
    }
}
