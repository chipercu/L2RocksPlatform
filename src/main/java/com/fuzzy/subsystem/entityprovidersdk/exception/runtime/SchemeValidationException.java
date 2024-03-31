package com.fuzzy.subsystem.entityprovidersdk.exception.runtime;

public class SchemeValidationException extends RuntimeException {

    public SchemeValidationException(String message) {
        super(message);
    }

    public SchemeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemeValidationException(Throwable cause) {
        super(cause);
    }
}
