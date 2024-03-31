package com.fuzzy.database.utils;

import com.fuzzy.database.utils.InstantUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeUtils {

    private static final ZoneOffset OFFSET = ZoneOffset.UTC;

    public static final LocalDateTime MAX = fromInstant(com.fuzzy.database.utils.InstantUtils.MAX);
    public static final LocalDateTime MIN =  fromInstant(com.fuzzy.database.utils.InstantUtils.MIN);

    public static long toLong(LocalDateTime value) {
        return com.fuzzy.database.utils.InstantUtils.toLong(value.toInstant(OFFSET));
    }

    public static LocalDateTime fromLong(long value) {
        return fromInstant(InstantUtils.fromLong(value));
    }

    private static LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), OFFSET);
    }
}
