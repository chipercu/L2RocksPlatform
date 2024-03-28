package com.fuzzy.subsystems.syslog;

import com.google.common.base.CharMatcher;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SysLogUtils {

    public static boolean isAscii(@NonNull String value) {
        return CharMatcher.ascii().matchesAllOf(value);
    }

    public static boolean isAsciiAndCorrectLength(@NonNull String value, int maxLength) {
        return !value.isEmpty() && value.length() <= maxLength && isAscii(value);
    }
}
