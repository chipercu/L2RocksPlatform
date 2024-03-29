package com.fuzzy.subsystem.database;

import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.component.database.DatabaseComponentExtension;
import com.fuzzy.subsystems.scheduler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class DatabaseComponentExtensionImpl implements DatabaseComponentExtension {

    public final DatabaseConfig config;
    private final Scheduler scheduler;
    private final CronTrigger cronTrigger;

    private SubsystemScheduler subsystemScheduler;
    private DatabaseComponent databaseComponent;
    private PeriodicalBackupJob periodicalBackupJob;
    private static final Logger log = LoggerFactory.getLogger(DatabaseComponentExtensionImpl.class);

    public DatabaseComponentExtensionImpl(DatabaseConfig config, Scheduler scheduler) {
        this.config = config;
        this.scheduler = scheduler;
        // Каждый день в 00:00:00
        cronTrigger = new SimpleCronTrigger("0 0 0 * * ?");
    }

    @Override
    public void initialize(DatabaseComponent databaseComponent) {
        //TODO Ulitin V. необходимо включить
        //Subsystems.getInstance().getQueryPool().execute(databaseComponent, new IntegrityCheckQuery());
    }

    @Override
    public void onStart(DatabaseComponent databaseComponent) {
        this.databaseComponent = databaseComponent;
        subsystemScheduler = new SubsystemScheduler(this.databaseComponent, scheduler);
        if (config.isPeriodicalBackupEnabled() && Objects.isNull(periodicalBackupJob)) {
            schedulePeriodicalBackupJob();
            log.info("Backup job created.");
        }
    }

    private void schedulePeriodicalBackupJob() {
        try {
            periodicalBackupJob = new PeriodicalBackupJob(databaseComponent, config);
            subsystemScheduler.scheduleJob(cronTrigger, () -> periodicalBackupJob);
        } catch (Throwable thr) {
            throw new RuntimeException(thr);
        }
    }
}
