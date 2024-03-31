package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;
import com.fuzzy.database.schema.dbstruct.DBTable;

public class TableAlreadyExistsException extends SchemaException {

    public TableAlreadyExistsException(DBTable table) {
        super("Table already exists, table=" + table.getName());
    }
}
