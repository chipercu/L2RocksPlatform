package com.fuzzy.subsystem.database;

import com.fuzzy.main.platform.component.database.DatabaseComponent;
import com.fuzzy.main.platform.component.database.DatabaseConsts;
import com.fuzzy.main.platform.component.database.remote.backup.RControllerBackup;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryFuture;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.subsystems.scheduler.Job;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeriodicalBackupJob extends Job {

    private final PeriodicalBackupMonitor periodicalBackupMonitor;
    private RControllerBackup rControllerBackup;
    private static final Logger log = LoggerFactory.getLogger(PeriodicalBackupJob.class);
    private static final String PERIODICAL_BACKUP_DIR = "periodical.backup";
    private final DatabaseConfig config;

    public PeriodicalBackupJob(DatabaseComponent databaseComponent, DatabaseConfig config) {
        periodicalBackupMonitor = new PeriodicalBackupMonitor(databaseComponent);
        this.config = config;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        rControllerBackup = resources.getQueryRemoteController(
                DatabaseConsts.UUID, RControllerBackup.class);
        //rControllerBackup = resources.getQueryRemoteController( TODO Ulitin V.  перевести на этот механизм
        //        DatabaseComponent.class, RControllerBackup.class);
    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        log.info("Periodical backup job started");
        rControllerBackup.createBackup(
                config.getBackupDir().toString(),
                PERIODICAL_BACKUP_DIR
        );
        return null;
    }

    @Override
    public void postAction(@NonNull QueryFuture<Void> queryFuture) {
        queryFuture.thenRun(() -> log.info("Periodical backup successes"))
                .exceptionally(throwable -> {
                    log.error("Periodical backup failed", throwable);
                    try {
                        periodicalBackupMonitor.captureEvent(
                                "Periodical backup failed: \n" + ExceptionUtils.getStackTrace(throwable)
                        );
                    } catch (PlatformException e) {
                        log.error("Periodical backup failed", e);
                    }
                    return null;
                });
    }
}