package com.fuzzy.main.entityprovidersdk.exception.runtime;

public class DeserializerException extends RuntimeException {

    public DeserializerException(String message) {
        super(message);
    }

    public DeserializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeserializerException(Throwable cause) {
        super(cause);
    }
}
