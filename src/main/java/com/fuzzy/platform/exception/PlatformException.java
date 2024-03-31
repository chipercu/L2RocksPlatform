package com.fuzzy.platform.exception;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class PlatformException extends Exception {

    private final String componentUuid;
    private final String code;
    private final Map<String, Object> parameters;
    private final String comment;

    PlatformException(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        this(null, code, comment, parameters, cause);
    }

    PlatformException(String code, String comment, Map<String, Object> parameters) {
        this(null, code, comment, parameters);
    }

    PlatformException(String subsystemUuid, String code, String comment, Map<String, Object> parameters) {
        this(subsystemUuid, code, comment, parameters, null);
    }

    PlatformException(String componentUuid, String code, String comment, Map<String, Object> parameters, Throwable cause) {
        super(
                buildMessage(componentUuid, code, parameters, comment),
                cause
        );

        if (componentUuid != null && componentUuid.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (code==null || code.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.componentUuid = componentUuid;
        this.code = code;
        this.parameters = parameters == null ? null : Collections.unmodifiableMap(parameters);
        this.comment = comment;
    }

    public String getComponentUuid() {
        return componentUuid;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public <T> T getParameterValue(String paramName, T defaultValue) {
        return parameters != null ? (T) parameters.getOrDefault(paramName, defaultValue) : defaultValue;
    }

    public String getComment() {
        return comment;
    }

    private static String buildMessage(String subsystemUuid, String code, Map<String, Object> parameters, String comment) {
        StringJoiner builder = new StringJoiner(", ");
        if (comment != null) builder.add(comment);
        if (subsystemUuid != null) builder.add("subsystemUuid=" + subsystemUuid);
        builder.add("code=" + code);
        if (parameters != null) builder.add("parameters=" + parameters);
        return builder.toString();
    }

    public static boolean equals(PlatformException e1, PlatformException e2) {
        if (e1 == e2) {
            return true;
        } else if (e1 == null || e2 == null) {
            return false;
        }

        if (!Objects.equals(e1.getComponentUuid(), e2.getComponentUuid())) {
            return false;
        }

        if (!e1.getCode().equals(e2.getCode())) {
            return false;
        }

        if (!Objects.equals(e1.getComment(), e2.getComment())) {
            return false;
        }

        if (!Objects.equals(e1.getParameters(), e2.getParameters())) {
            return false;
        }

        return Objects.equals(e1.getCause(), e2.getCause());
    }
}
