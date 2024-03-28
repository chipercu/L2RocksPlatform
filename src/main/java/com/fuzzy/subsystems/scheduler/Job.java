package com.fuzzy.subsystems.scheduler;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.Query;
import com.fuzzy.main.platform.querypool.QueryFuture;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Job extends Query<Void> {

    private static final Logger log = LoggerFactory.getLogger(Job.class);

    public void postAction(@NonNull QueryFuture<Void> queryFuture) {

    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        try {
            executeJob(transaction);
        } catch (Throwable e) {
            log.error("Exception while executing scheduled job", e);
            throw e;
        }
        return null;
    }

    public Void executeJob(QueryTransaction transaction) throws PlatformException {
        return null;
    }
}
