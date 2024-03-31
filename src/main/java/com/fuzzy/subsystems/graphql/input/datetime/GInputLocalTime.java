package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.out.GOutTime;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.LocalTime;

@GraphQLTypeInput("input_local_time")
public class GInputLocalTime implements Comparable<GInputLocalTime>, RemoteObject {

    public static @NonNull GInputLocalTime midnight() throws PlatformException {
        return new GInputLocalTime(0, 0, 0);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String SECOND = "second";

    private final Integer hour;
    private final Integer minute;
    private final Integer second;
    private LocalTime localTime;

    public GInputLocalTime(
            @GraphQLDescription("Час")
            @NonNull @GraphQLName(HOUR) Integer hour,
            @GraphQLDescription("Минута")
            @NonNull @GraphQLName(MINUTE) Integer minute,
            @GraphQLDescription("Секунда")
            @NonNull @GraphQLName(SECOND) Integer second) throws PlatformException {
        validateHour(hour);
        validateMinute(minute);
        validateSecond(second);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.localTime = null;
    }

    public @NonNull Integer getHour() {
        return hour;
    }

    public @NonNull Integer getMinute() {
        return minute;
    }

    public @NonNull Integer getSecond() {
        return second;
    }

    public boolean isBefore(@NonNull GInputLocalTime when) {
        return  getLocalTime().isBefore(when.getLocalTime());
    }

    public boolean isAfter(@NonNull GInputLocalTime when) {
        return getLocalTime().isAfter(when.getLocalTime());
    }

    @Override
    public int compareTo(@NonNull GInputLocalTime o) {
        return getLocalTime().compareTo(o.getLocalTime());
    }

    public @NonNull LocalTime getLocalTime() {
        if (localTime == null) {
            localTime = LocalTime.of(getHour(), getMinute(), getSecond());
        }
        return localTime;
    }

    public @NonNull GOutTime toOutTime() {
        return new GOutTime(hour, minute, second);
    }

    private static void validateHour(@NonNull Integer hour) throws PlatformException {
        if (hour < 0 || hour > 23) {
            throw GeneralExceptionBuilder.buildInvalidValueException(HOUR, hour);
        }
    }

    private static void validateMinute(@NonNull Integer minute) throws PlatformException {
        if (minute < 0 || minute > 59) {
            throw GeneralExceptionBuilder.buildInvalidValueException(MINUTE, minute);
        }
    }

    private static void validateSecond(@NonNull Integer second) throws PlatformException {
        if (second < 0 || second > 59) {
            throw GeneralExceptionBuilder.buildInvalidValueException(SECOND, second);
        }
    }
}