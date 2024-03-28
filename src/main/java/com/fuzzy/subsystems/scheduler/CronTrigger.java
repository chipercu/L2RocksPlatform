package com.fuzzy.subsystems.scheduler;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.TimeZone;

public interface CronTrigger {

    @NonNull TimeZone getTimeZone();

    @NonNull String getCronExpression();
}
