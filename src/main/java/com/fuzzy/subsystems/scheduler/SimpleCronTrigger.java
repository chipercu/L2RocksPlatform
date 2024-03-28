package com.fuzzy.subsystems.scheduler;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class SimpleCronTrigger implements CronTrigger {

    private final TimeZone timeZone;
    private final String cronExpression;

    public SimpleCronTrigger(@NonNull TimeZone timeZone, @NonNull String cronExpression) {
        this.timeZone = timeZone;
        this.cronExpression = cronExpression;
    }

    public SimpleCronTrigger(@NonNull String cronExpression) {
        this(TimeZone.getDefault(), cronExpression);
    }

    @Override
    public @NonNull TimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public @NonNull String getCronExpression() {
        return cronExpression;
    }

    public static boolean isValidCronExpression(String cron) {
        return CronExpression.isValidExpression(cron);
    }

    public @Nullable Instant getTimeAfter(@NonNull Instant date) {
        try {
            CronExpression cronExpression = new CronExpression(this.cronExpression);
            cronExpression.setTimeZone(this.timeZone);
            Date dateAfter = cronExpression.getTimeAfter(Date.from(date));
            if (dateAfter == null) {
                return null;
            }
            return dateAfter.toInstant();
        } catch (ParseException e) {
            return null;
        }
    }
}
