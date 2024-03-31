package com.fuzzy.platform.exception;

import com.fuzzy.platform.exception.PlatformException;

import java.util.Map;

public abstract class ExceptionFactory {

    public abstract PlatformException build(String code, String comment, Map<String, Object> parameters, Throwable cause);

    public PlatformException build(String code, String comment, Map<String, Object> parameters) {
        return build(code, comment, parameters, null);
    }

    public PlatformException build(String code, Map<String, Object> parameters) {
        return build(code, null, parameters, null);
    }

    public PlatformException build(String code, String comment) {
        return build(code, comment, null, null);
    }

    public PlatformException build(String code, Throwable e) {
        return build(code,  e != null ? e.getMessage() : null, null, e);
    }

    public PlatformException build(String code, Throwable e, Map<String, Object> parameters) {
        return build(code,  e != null ? e.getMessage() : null, parameters, e);
    }

    public PlatformException build(String code) {
        return build(code, null, null, null);
    }
}
