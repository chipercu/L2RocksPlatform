package com.fuzzy.subsystems.scheduler;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.Instant;

public class SimpleRepeatableTrigger implements RepeatableTrigger {

    private final Instant startTime;
    private final Duration repeatInterval;
    private final int repeatCount;

    public SimpleRepeatableTrigger(@NonNull Instant startTime,
                                   @NonNull Duration repeatInterval,
                                   int repeatCount) {
        this.startTime = startTime;
        this.repeatInterval = repeatInterval;
        this.repeatCount = repeatCount;
    }

    public SimpleRepeatableTrigger(@NonNull Instant startTime,
                                   @NonNull Duration repeatInterval) {
        this(startTime, repeatInterval, REPEAT_INDEFINITELY);
    }

    @Override
    public @NonNull Instant getStartTime() {
        return startTime;
    }

    @Override
    public @NonNull Duration getRepeatInterval() {
        return repeatInterval;
    }

    @Override
    public int getRepeatCount() {
        return repeatCount;
    }
}
