package com.fuzzy.subsystem.database.scheduler.backup;

import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.time.Duration;
import java.util.Calendar;

public class PeriodicalBackupTrigger extends SimpleTriggerImpl {

    public PeriodicalBackupTrigger() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        setStartTime(calendar.getTime());
        setRepeatInterval(Duration.ofDays(1).toMillis());
        setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    }
}
