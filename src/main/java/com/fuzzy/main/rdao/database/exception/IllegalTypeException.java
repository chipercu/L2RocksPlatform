package com.fuzzy.main.rdao.database.exception;

public class IllegalTypeException extends RuntimeException {

    public IllegalTypeException(String message) {
        super(message);
    }

    public IllegalTypeException(Throwable cause) {
        super(cause);
    }

    public IllegalTypeException(Class expected, Class actual) {
        super("Expected type " + expected + " but actual type " + actual);
    }
}
