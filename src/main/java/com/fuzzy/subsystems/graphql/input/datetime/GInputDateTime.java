package com.fuzzy.subsystems.graphql.input.datetime;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLName;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeInput;
import com.fuzzy.main.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@GraphQLTypeInput("input_date_time")
public class GInputDateTime implements Comparable<GInputDateTime>, RemoteObject {

    private final GInputLocalDate localDate;
    private final GInputLocalTime localTime;
    private final Integer offset;
    private Instant instant;
    private Instant utcInstant;

    public GInputDateTime(
            @GraphQLDescription(Tool.LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.LOCAL_DATE)
            GInputLocalDate localDate,
            @GraphQLDescription(Tool.LOCAL_TIME_DESCRIPTION)
            @NonNull @GraphQLName(Tool.LOCAL_TIME) GInputLocalTime localTime,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateOffset(offset);
        this.localDate = localDate;
        this.localTime = localTime;
        this.offset = offset;
        this.instant = null;
        this.utcInstant = null;
    }

    public @NonNull GInputLocalDate getLocalDate() {
        return localDate;
    }

    public @NonNull GInputLocalTime getLocalTime() {
        return localTime;
    }

    public @NonNull Integer getOffset() {
        return offset;
    }

    public @NonNull Instant getInstant() {
        if (instant == null) {
            instant = toInstant(ZoneOffset.ofTotalSeconds(offset / 1000));
        }
        return instant;
    }

    public @NonNull Instant getUtcInstant() {
        if (utcInstant == null) {
            utcInstant = toInstant(ZoneOffset.UTC);
        }
        return utcInstant;
    }

    public boolean isBefore(@NonNull GInputDateTime other) {
        return getInstant().isBefore(other.getInstant());
    }

    public boolean isAfter(@NonNull GInputDateTime other) {
        return getInstant().isAfter(other.getInstant());
    }

    @Override
    public int compareTo(@NonNull GInputDateTime o) {
        return getInstant().compareTo(o.getInstant());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GInputDateTime gInputDateTime = (GInputDateTime) o;
        return Objects.equals(localDate, gInputDateTime.localDate)
                && Objects.equals(localTime, gInputDateTime.localTime)
                && Objects.equals(offset, gInputDateTime.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDate, localTime, offset);
    }

    private @NonNull Instant toInstant(@NonNull ZoneOffset offset) {
        return LocalDateTime.of(localDate.getLocalDate(), localTime.getLocalTime()).toInstant(offset);
    }
}
