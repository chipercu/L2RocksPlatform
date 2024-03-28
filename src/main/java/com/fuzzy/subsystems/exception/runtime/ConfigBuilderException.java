package com.fuzzy.subsystems.exception.runtime;

public class ConfigBuilderException extends RuntimeException {

    public ConfigBuilderException(String message) {
        super(message);
    }

    public ConfigBuilderException(Throwable cause) {
        super(cause);
    }

    public ConfigBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
