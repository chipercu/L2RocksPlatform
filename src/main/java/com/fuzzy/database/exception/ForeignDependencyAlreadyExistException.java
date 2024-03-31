package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class ForeignDependencyAlreadyExistException extends SchemaException {

    public ForeignDependencyAlreadyExistException(String fieldName, String tableName) {
        super("Field name=" + fieldName + " into '" + tableName + "' is already foreign key");
    }
}