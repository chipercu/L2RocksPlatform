package com.fuzzy.database.exception;

import com.fuzzy.database.exception.SchemaException;

public class InconsistentTableException extends SchemaException {

    public InconsistentTableException(String message) {
        super(message);
    }
}
