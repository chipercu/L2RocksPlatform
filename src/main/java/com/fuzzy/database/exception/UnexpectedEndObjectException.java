package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class UnexpectedEndObjectException extends DatabaseException {

    public UnexpectedEndObjectException(long prevId, long nextId, String fieldName) {
        super("Unexpected end of object. Previous id of object: " + prevId + ". Next key: id = " + nextId + ", field = " + fieldName);
    }
}
