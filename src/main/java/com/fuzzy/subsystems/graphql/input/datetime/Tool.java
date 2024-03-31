package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

class Tool {

    public static final String LOCAL_DATE = "local_date";
    public static final String LOCAL_DATE_DESCRIPTION = "Локальная дата";

    public static final String LOCAL_TIME = "local_time";
    public static final String LOCAL_TIME_DESCRIPTION = "Локальное время";

    public static final String OFFSET = "offset";
    public static final String OFFSET_DESCRIPTION = "Смещение (мс)";

    public static final String BEGIN_LOCAL_DATE = "begin_local_date";
    public static final String BEGIN_LOCAL_DATE_DESCRIPTION = "Начальная локальная дата";

    public static final String END_LOCAL_DATE = "end_local_date";
    public static final String END_LOCAL_DATE_DESCRIPTION = "Конечная локальная дата";

    public static final String BEGIN_LOCAL_TIME = "begin_local_time";
    public static final String BEGIN_LOCAL_TIME_DESCRIPTION = "Начальное локальное время";

    public static final String END_LOCAL_TIME = "end_local_time";
    public static final String END_LOCAL_TIME_DESCRIPTION = "Конечное локальное время";

    public static void validateOffset(@NonNull Integer offset) throws PlatformException {
        long maxOffsetMs = Duration.ofHours(18).toMillis();
        if (offset < -maxOffsetMs || offset > maxOffsetMs) {
            throw GeneralExceptionBuilder.buildInvalidValueException(OFFSET, offset);
        }
    }

    public static void validateDateInterval(@NonNull GInputLocalDate beginLocalDate,
                                            @NonNull GInputLocalDate endLocalDate,
                                            @NonNull Integer offset) throws PlatformException {
        validateOffset(offset);
        if (endLocalDate.isBefore(beginLocalDate)) {
            throw GeneralExceptionBuilder.buildInvalidValueException("[begin, end]", "the begin must be less or equal the end");
        }
    }

    public static void validateTimeInterval(@NonNull GInputLocalTime beginLocalTime,
                                            @NonNull GInputLocalTime endLocalTime,
                                            @NonNull Integer offset) throws PlatformException {
        validateOffset(offset);
        if (!endLocalTime.isAfter(beginLocalTime)) {
            throw GeneralExceptionBuilder.buildInvalidValueException("[begin, end]", "the begin must be less the end");
        }
    }
}
