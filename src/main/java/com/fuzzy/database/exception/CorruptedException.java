package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class CorruptedException extends DatabaseException {

    public CorruptedException(String message) {
        super(message);
    }
}
