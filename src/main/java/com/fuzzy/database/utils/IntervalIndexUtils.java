package com.fuzzy.database.utils;

import com.fuzzy.database.exception.UnsupportedTypeException;
import com.fuzzy.database.utils.InstantUtils;
import com.fuzzy.database.utils.LocalDateTimeUtils;

import java.time.Instant;
import java.time.LocalDateTime;

public class IntervalIndexUtils {

    public static long castToLong(long value) {
        return value;
    }

    public static long castToLong(double value) {
        long val = Double.doubleToRawLongBits(value);
        return val < 0 ? 0x8000000000000000L - val : val;
    }

    public static long castToLong(Instant value) {
        return InstantUtils.toLong(value);
    }

    public static long castToLong(LocalDateTime value) {
        return LocalDateTimeUtils.toLong(value);
    }

    public static long castToLong(Object value) {
        if (value == null) {
            return 0;
        }

        if (value.getClass() == Long.class) {
            return castToLong(((Long) value).longValue());
        } else if (value.getClass() == Instant.class) {
            return castToLong((Instant) value);
        } else if (value.getClass() == Double.class) {
            return castToLong(((Double) value).doubleValue());
        } else if (value.getClass() == LocalDateTime.class) {
            return castToLong((LocalDateTime) value);
        }

        throw new UnsupportedTypeException(value.getClass());
    }

    public static <T> void checkType(Class<T> indexedClass) {
        if (indexedClass == Long.class ||
                indexedClass == Instant.class ||
                indexedClass == Double.class ||
                indexedClass == LocalDateTime.class) {
            return;
        }

        throw new UnsupportedTypeException(indexedClass);
    }

    public static void checkInterval(long begin, long end) {
        if (begin > end) {
            throw new IllegalArgumentException("begin = " + begin + " greater than end = " + end);
        }
    }
}
