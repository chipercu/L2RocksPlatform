package com.fuzzy.subsystem.database;

import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.component.database.DatabaseConsts;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.remote.systemevent.RControllerSystemEvent;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;

import java.time.Duration;

public class PeriodicalBackupMonitor {


    private final Duration ttl;
    private final RControllerSystemEvent rControllerSystemEvent;
    private static final String EVENT_TYPE = "PERIODICAL_BACKUP";


    public PeriodicalBackupMonitor(DatabaseComponent component) {
        rControllerSystemEvent = component.getRemotes().get(CoreSubsystem.class, RControllerSystemEvent.class);
        //23 часа 59 минут
        ttl = Duration.ofSeconds(60 * 60 * 24 - 60);
    }

    public void captureEvent(String message) throws PlatformException {

        rControllerSystemEvent.captureEvent(SystemEvent.newBuilder()
                .withEventType(EVENT_TYPE)
                .withLevel(SystemEvent.EventLevel.CRITICAL)
                .withSubsystemUuid(DatabaseConsts.UUID)
                .withMessage(message)
                .withTtl(ttl)
                .build());
    }
}