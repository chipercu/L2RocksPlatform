package com.fuzzy.database.exception;

public class ClosedObjectException extends RuntimeException {

    private final Class causeClass;

    public ClosedObjectException(Class causeClass) {
        this.causeClass = causeClass;
    }

    public Class getCauseClass() {
        return causeClass;
    }
}

