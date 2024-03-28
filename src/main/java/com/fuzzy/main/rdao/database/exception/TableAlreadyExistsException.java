package com.fuzzy.main.rdao.database.exception;

import com.fuzzy.main.rdao.database.schema.dbstruct.DBTable;

public class TableAlreadyExistsException extends SchemaException {

    public TableAlreadyExistsException(DBTable table) {
        super("Table already exists, table=" + table.getName());
    }
}
