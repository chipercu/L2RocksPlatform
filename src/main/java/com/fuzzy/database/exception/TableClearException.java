package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class TableClearException extends SchemaException
{
    public TableClearException(String message) {
        super(message);
    }
}
