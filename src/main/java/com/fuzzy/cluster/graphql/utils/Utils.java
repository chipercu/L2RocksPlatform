package com.fuzzy.cluster.graphql.utils;

public class Utils {

    private static final Double DOUBLE_DEFAULT = 0d;
    private static final Float FLOAT_DEFAULT = 0f;

    private Utils() {
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static <T> T defaultValue(Class<T> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return (T) Boolean.FALSE;
            } else if (type == char.class) {
                return (T) Character.valueOf('\0');
            } else if (type == byte.class) {
                return (T) Byte.valueOf((byte) 0);
            } else if (type == short.class) {
                return (T) Short.valueOf((short) 0);
            } else if (type == int.class) {
                return (T) Integer.valueOf(0);
            } else if (type == long.class) {
                return (T) Long.valueOf(0L);
            } else if (type == float.class) {
                return (T) FLOAT_DEFAULT;
            } else if (type == double.class) {
                return (T) DOUBLE_DEFAULT;
            }
        }
        return null;
    }
}
