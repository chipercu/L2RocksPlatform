package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class InvalidValueException extends DatabaseException {

    public InvalidValueException(String message) {
        super(message);
    }
}
