package com.fuzzy.main.rdao.database.exception;

public class ForeignDependencyNotFoundException extends SchemaException {

    public ForeignDependencyNotFoundException(String fieldName, String tableName) {
        super("Field name=" + fieldName + " into '" + tableName + "' is not a foreign key");
    }
}