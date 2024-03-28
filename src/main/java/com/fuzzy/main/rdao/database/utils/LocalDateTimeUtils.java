package com.fuzzy.main.rdao.database.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeUtils {

    private static final ZoneOffset OFFSET = ZoneOffset.UTC;

    public static final LocalDateTime MAX = fromInstant(InstantUtils.MAX);
    public static final LocalDateTime MIN =  fromInstant(InstantUtils.MIN);

    public static long toLong(LocalDateTime value) {
        return InstantUtils.toLong(value.toInstant(OFFSET));
    }

    public static LocalDateTime fromLong(long value) {
        return fromInstant(InstantUtils.fromLong(value));
    }

    private static LocalDateTime fromInstant(Instant instant) {
        return LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), OFFSET);
    }
}
