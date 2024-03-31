package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class SequenceAlreadyExistsException extends DatabaseException {

    public SequenceAlreadyExistsException(String name) {
        super("Sequence " + name + " already exists.");
    }
}
