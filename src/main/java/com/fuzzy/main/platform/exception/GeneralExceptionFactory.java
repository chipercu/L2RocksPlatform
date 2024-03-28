package com.fuzzy.main.platform.exception;

import java.util.Map;

//TODO Ulitin V. Убрать public class
public class GeneralExceptionFactory extends ExceptionFactory {

    @Override
    public PlatformException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        if (cause != null) {
            return new PlatformException(code, comment, parameters, cause);
        }
        return new PlatformException(code, comment, parameters);
    }
}
