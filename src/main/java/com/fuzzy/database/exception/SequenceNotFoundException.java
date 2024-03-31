package com.fuzzy.database.exception;

import com.fuzzy.database.exception.DatabaseException;

public class SequenceNotFoundException extends DatabaseException {

    public SequenceNotFoundException(String name) {
        super("Sequence " + name + " not found.");
    }
}
