package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class TableRemoveException extends SchemaException
{
    public TableRemoveException(String message) {
        super(message);
    }
}
