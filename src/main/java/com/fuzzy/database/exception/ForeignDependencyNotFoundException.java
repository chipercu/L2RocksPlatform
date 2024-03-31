package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class ForeignDependencyNotFoundException extends SchemaException {

    public ForeignDependencyNotFoundException(String fieldName, String tableName) {
        super("Field name=" + fieldName + " into '" + tableName + "' is not a foreign key");
    }
}