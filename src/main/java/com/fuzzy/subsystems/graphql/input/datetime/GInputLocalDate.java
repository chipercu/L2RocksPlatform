package com.fuzzy.subsystems.graphql.input.datetime;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.graphql.input.GMonth;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.LocalDate;
import java.time.YearMonth;

@GraphQLTypeInput("input_local_date")
public class GInputLocalDate implements Comparable<GInputLocalDate>, RemoteObject {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final Integer year;
    private final GMonth month;
    private final Integer day;
    private LocalDate localDate;

    public GInputLocalDate(
            @GraphQLDescription("День")
            @NonNull @GraphQLName(DAY) Integer day,
            @GraphQLDescription("Месяц")
            @NonNull @GraphQLName(MONTH) GMonth month,
            @GraphQLDescription("Год")
            @NonNull @GraphQLName(YEAR) Integer year) throws PlatformException {
        validateYear(year);
        validateDate(day, month, year);
        this.day = day;
        this.month = month;
        this.year = year;
        this.localDate = null;
    }

    public int getYear() {
        return year;
    }

    public @NonNull GMonth getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public @NonNull LocalDate getLocalDate() {
        if (localDate == null) {
            localDate = LocalDate.of(getYear(), getMonth().intValue(), getDay());
        }
        return localDate;
    }

    public boolean isBefore(@NonNull GInputLocalDate other) {
        return getLocalDate().isBefore(other.getLocalDate());
    }

    public boolean isAfter(@NonNull GInputLocalDate other) {
        return getLocalDate().isAfter(other.getLocalDate());
    }

    @Override
    public int compareTo(@NonNull GInputLocalDate o) {
        return getLocalDate().compareTo(o.getLocalDate());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GInputLocalDate gInputDate = (GInputLocalDate) o;

        if (!year.equals(gInputDate.year)) return false;
        if (!month.equals(gInputDate.month)) return false;
        return day.equals(gInputDate.day);
    }

    @Override
    public int hashCode() {
        int result = year.hashCode();
        result = 31 * result + month.hashCode();
        result = 31 * result + day.hashCode();
        return result;
    }

    private static void validateYear(@NonNull Integer year) throws PlatformException {
        if (year < 0) {
            throw GeneralExceptionBuilder.buildInvalidValueException(YEAR, year);
        }
    }

    private static void validateDate(@NonNull Integer day, @NonNull GMonth month, @NonNull Integer year) throws PlatformException {
        if (!YearMonth.of(year, month.getValue()).isValidDay(day)) {
            throw GeneralExceptionBuilder.buildInvalidValueException(DAY, day);
        }
    }

    @Override
    public @NonNull String toString() {
        return day + "." + String.format("%tm", month.getValue()) + "." + year;
    }
}
