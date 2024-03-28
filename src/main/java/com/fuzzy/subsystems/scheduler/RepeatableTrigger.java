package com.fuzzy.subsystems.scheduler;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.time.Instant;

public interface RepeatableTrigger {

    int REPEAT_INDEFINITELY = -1;

    @NonNull Instant getStartTime();

    @NonNull Duration getRepeatInterval();

    int getRepeatCount();
}
