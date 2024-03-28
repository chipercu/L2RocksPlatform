package com.fuzzy.main.rdao.database.exception;

public class SequenceAlreadyExistsException extends DatabaseException {

    public SequenceAlreadyExistsException(String name) {
        super("Sequence " + name + " already exists.");
    }
}
