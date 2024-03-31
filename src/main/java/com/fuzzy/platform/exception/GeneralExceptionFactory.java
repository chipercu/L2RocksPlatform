package com.fuzzy.platform.exception;

import com.fuzzy.platform.exception.ExceptionFactory;
import com.fuzzy.platform.exception.PlatformException;

import java.util.Map;

//TODO Ulitin V. Убрать public class
public class GeneralExceptionFactory extends ExceptionFactory {

    @Override
    public com.fuzzy.platform.exception.PlatformException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        if (cause != null) {
            return new com.fuzzy.platform.exception.PlatformException(code, comment, parameters, cause);
        }
        return new PlatformException(code, comment, parameters);
    }
}
