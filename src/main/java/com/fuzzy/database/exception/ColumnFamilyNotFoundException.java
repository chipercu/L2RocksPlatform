package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class ColumnFamilyNotFoundException extends DatabaseException {

    public ColumnFamilyNotFoundException(String columnFamily) {
        super("Column family " + columnFamily + " not found.");
    }
}
