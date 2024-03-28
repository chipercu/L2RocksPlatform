package com.fuzzy.main.rdao.database.exception;

public class UnsupportedTypeException extends IllegalTypeException {

    public UnsupportedTypeException(Class type) {
        super("Unsupported type " + type);
    }
}
