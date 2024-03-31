package com.fuzzy.platform.exception;

import com.fuzzy.platform.exception.ExceptionFactory;
import com.fuzzy.platform.exception.PlatformException;

import java.util.Map;

public class PlatformExceptionFactory extends ExceptionFactory {

    private final String componentUuid;

    public PlatformExceptionFactory(String componentUuid) {
        this.componentUuid = componentUuid;
    }

    public String getComponentUuid() {
        return componentUuid;
    }

    @Override
    public com.fuzzy.platform.exception.PlatformException build(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        if (cause != null) {
            return new com.fuzzy.platform.exception.PlatformException(componentUuid, code, comment, parameters, cause);
        }
        return new PlatformException(componentUuid, code, comment, parameters);
    }
}
