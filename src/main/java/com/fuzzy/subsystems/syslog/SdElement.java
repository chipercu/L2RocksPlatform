package com.fuzzy.subsystems.syslog;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

class SdElement extends SysLogItem {

    private StringBuilder data;

    public SdElement(@NonNull String id) {
        validateName(id);
        data = new StringBuilder();
        data.append('[').append(id);
    }

    public SdElement addParam(@NonNull String name, String value) {
        if (value == null) {
            value = StringUtils.EMPTY;
        }
        validateName(name);
        data.append(' ').append(name).append("=\"").append(escapeValue(value)).append('\"');
        return this;
    }

    public @NonNull String getData() {
        return data.toString() + ']';
    }

    private static void validateName(String sdName) {
        validateAsciiAndLength(sdName, 32);
        if (CharMatcher.anyOf("= ]\"").matchesAnyOf(sdName)) {
            throw new IllegalArgumentException(sdName + " contains illegal symbol");
        }
    }

    private static String escapeValue(String value) {
        if (CharMatcher.anyOf("\"]\\").matchesAnyOf(value)) {
            StringBuilder builder = new StringBuilder(value.length());
            for (int i = 0; i < value.length(); i++) {
                char symbol = value.charAt(i);
                switch (symbol) {
                    case '\"':
                    case ']':
                    case '\\':
                        builder.append("\\");
                        break;
                }
                builder.append(symbol);
            }
            return builder.toString();
        }
        return value;
    }
}
