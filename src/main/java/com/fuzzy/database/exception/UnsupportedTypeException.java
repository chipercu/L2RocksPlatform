package com.fuzzy.database.exception;

import com.fuzzy.database.exception.IllegalTypeException;

public class UnsupportedTypeException extends IllegalTypeException {

    public UnsupportedTypeException(Class type) {
        super("Unsupported type " + type);
    }
}
