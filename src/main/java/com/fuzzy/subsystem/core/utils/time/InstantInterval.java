package com.fuzzy.subsystem.core.utils.time;

import com.fuzzy.database.utils.InstantUtils;

import java.time.Duration;
import java.time.Instant;

public class InstantInterval {

    public static final InstantInterval EMPTY = new InstantInterval();
    public static final InstantInterval MAX = new InstantInterval(InstantUtils.ZERO, InstantUtils.MAX);

    private final Instant begin;
    private final Instant end;

    private InstantInterval() {
        this.begin = Instant.MIN;
        this.end = Instant.MIN;
    }

    /**
     * @throws IllegalArgumentException if begin greater than end
     */
    public InstantInterval(Instant begin, Instant end) {
        if (begin.compareTo(end) > 0) {
            throw new IllegalArgumentException("Begin greater than end");
        }
        this.begin = begin;
        this.end = end;
    }

    public Instant getBegin() {
        return begin;
    }

    public Instant getEnd() {
        return end;
    }

    public Duration getDuration() {
       return Duration.between(begin, end);
    }

    public boolean contains(final Instant value) {
        return !begin.isAfter(value) && value.isBefore(end);
    }

    public boolean contains(final InstantInterval target) {
        return target.begin.isAfter(begin) && target.end.isBefore(end);
    }

    public InstantInterval join(final InstantInterval value) {
        if (this.contains(value)) {
            return this;
        }

        if (value.contains(this)) {
            return value;
        }

        return new InstantInterval(
                begin.isBefore(value.begin) ? begin : value.begin,
                end.isAfter(value.end) ? end : value.end
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstantInterval)) return false;

        InstantInterval that = (InstantInterval) o;

        if (!begin.equals(that.begin)) return false;
        return end.equals(that.end);
    }

    @Override
    public int hashCode() {
        int result = begin.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
