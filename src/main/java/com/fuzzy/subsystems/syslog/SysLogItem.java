package com.fuzzy.subsystems.syslog;

class SysLogItem {

    protected static final String NIL_VALUE = "-";

    protected static void validateAscii(String value) {
        if (!SysLogUtils.isAscii(value)) {
            throw new IllegalArgumentException(value + " must be ASCII-string");
        }
    }

    protected static void validateAsciiAndLength(String value, int maxLength) {
        validateAscii(value);
        if (value.isEmpty() || value.length() > maxLength) {
            throw new IllegalArgumentException("Length of " + value + " must be 1.." + maxLength);
        }
    }

    protected static String validateAndGet(String value, int maxLength) {
        if (value == null) {
            return NIL_VALUE;
        }
        validateAsciiAndLength(value, maxLength);
        return value;
    }
}
