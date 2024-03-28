package com.fuzzy.main.rdao.database.exception;

public class FieldValueNotFoundException extends RuntimeException {

    public FieldValueNotFoundException(String fieldName) {
        super(fieldName);
    }
}
