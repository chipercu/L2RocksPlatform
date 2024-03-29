package com.fuzzy.subsystem.core.scheduler.systemevents;

import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystems.scheduler.RepeatableTrigger;
import com.fuzzy.subsystems.scheduler.SimpleRepeatableTrigger;

import java.time.Duration;
import java.time.Instant;

public class MailSendSystemEventTriggerBuilder {

    public static RepeatableTrigger build(CoreSubsystem coreSubsystem) {

        final Instant startTime = Instant.now()
                .plusSeconds(coreSubsystem.getConfig()
                        .getMailSendSystemEventsConfig()
                        .getDelay()
                        .getSeconds());
        final Duration interval = coreSubsystem.getConfig()
                .getMailSendSystemEventsConfig()
                .getInterval();

        return new SimpleRepeatableTrigger(startTime, interval);
    }
}