package com.fuzzy.database.exception;

public class FieldValueNotFoundException extends RuntimeException {

    public FieldValueNotFoundException(String fieldName) {
        super(fieldName);
    }
}
