package com.fuzzy.subsystems.syslog;

import com.google.common.base.CharMatcher;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserSdElement extends SdElement{

    public UserSdElement(@NonNull String id) {
        super(id);
        validateId(id);
    }

    private static void validateId(@NonNull String id) {
        String[] parts = id.split("@");
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new IllegalArgumentException(id + " is invalid");
        }
        for (int i = 0; i < parts[0].length(); i++) {
            char symbol = parts[0].charAt(i);
            if (symbol < 32 || symbol == 127 || symbol == '@') {
                throw new IllegalArgumentException(parts[0] + " contains illegal symbol");
            }
        }
        if (!CharMatcher.inRange('0', '9').matchesAllOf(parts[1])) {
            throw new IllegalArgumentException(parts[1] + " must be number");
        }
    }
}
