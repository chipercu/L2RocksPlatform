package com.fuzzy.subsystems.utils;

import java.util.Objects;

public class BuildVersionUtils {

    public static final String DEFAULT_VALUE = "UNKNOWN";

    public static String readBuildVersion(String source) {
        if (Objects.nonNull(source) && !source.isEmpty() && !source.equals(DEFAULT_VALUE)) {
            return source;
        }
        return null;
    }
}