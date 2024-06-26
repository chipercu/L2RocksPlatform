package com.fuzzy.platform.exception.runtime;

import com.fuzzy.platform.exception.PlatformException;

public class PlatformRuntimeException extends RuntimeException {

    private PlatformException platformException;

    public PlatformRuntimeException(PlatformException platformException) {
        super(platformException);
        this.platformException = platformException;
    }

    public PlatformRuntimeException(String message, PlatformException platformException) {
        super(message, platformException);
        this.platformException = platformException;
    }

    public PlatformException getPlatformException() {
        return platformException;
    }
}
