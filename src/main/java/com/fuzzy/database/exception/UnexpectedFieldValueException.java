package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class UnexpectedFieldValueException extends DatabaseException {

    public UnexpectedFieldValueException(String message) {
        super(message);
    }
}
