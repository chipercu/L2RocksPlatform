package com.fuzzy.main.rdao.database.exception;

public class TableNotFoundException extends SchemaException {

    public TableNotFoundException(String tableName) {
        super("Schema table name=" + tableName + " not found");
    }

    public TableNotFoundException(int tableId) {
        super("Table id=" + tableId + " not found");
    }
}
