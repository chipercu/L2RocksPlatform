package com.fuzzy.subsystems.graphql.input.datetime;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeInput;
import com.infomaximum.platform.exception.PlatformException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

@GraphQLTypeInput("input_date")
public class GInputDate implements Comparable<GInputDate>, RemoteObject {

    private final GInputLocalDate localDate;
    private final Integer offset;
    private Instant instant;
    private Instant utcInstant;

    public GInputDate(
            @GraphQLDescription(Tool.LOCAL_DATE_DESCRIPTION)
            @NonNull @GraphQLName(Tool.LOCAL_DATE) GInputLocalDate localDate,
            @GraphQLDescription(Tool.OFFSET_DESCRIPTION)
            @NonNull @GraphQLName(Tool.OFFSET) Integer offset) throws PlatformException {
        Tool.validateOffset(offset);
        this.localDate = localDate;
        this.offset = offset;
        this.instant = null;
        this.utcInstant = null;
    }

    public @NonNull GInputLocalDate getLocalDate() {
        return localDate;
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

    public boolean isBefore(@NonNull GInputDate other) {
        return getInstant().isBefore(other.getInstant());
    }

    public boolean isAfter(@NonNull GInputDate other) {
        return getInstant().isAfter(other.getInstant());
    }

    @Override
    public int compareTo(@NonNull GInputDate o) {
        return getInstant().compareTo(o.getInstant());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GInputDate gInputDate = (GInputDate) o;
        return Objects.equals(localDate, gInputDate.localDate) && Objects.equals(offset, gInputDate.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localDate, offset);
    }

    private @NonNull Instant toInstant(@NonNull ZoneOffset offset) {
        return localDate.getLocalDate().atStartOfDay().toInstant(offset);
    }
}
