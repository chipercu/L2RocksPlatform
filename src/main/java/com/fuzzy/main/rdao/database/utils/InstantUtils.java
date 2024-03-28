package com.fuzzy.main.rdao.database.utils;

import java.time.Instant;

public class InstantUtils {

    public static final Instant MAX = Instant.ofEpochMilli(Long.MAX_VALUE);
    public static final Instant ZERO = Instant.EPOCH;
    public static final Instant MIN = Instant.ofEpochMilli(Long.MIN_VALUE);

    public static long toLong(Instant value) {
        return value.toEpochMilli();
    }

    public static Instant fromLong(long value) {
        return Instant.ofEpochMilli(value);
    }
}
