package com.fuzzy.subsystem.database;

import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.maintenance.ChangeMode;
import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryPool;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.impl.ContextImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.securitylog.CoreEvent;
import com.fuzzy.subsystem.core.securitylog.CoreParameter;
import com.fuzzy.subsystem.core.securitylog.CoreTarget;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.security.SecurityLog;
import com.fuzzy.subsystems.security.struct.data.event.SyslogStructDataEvent;
import com.fuzzy.subsystems.security.struct.data.target.SyslogStructDataTarget;
import com.fuzzy.subsystems.subsystem.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrityCheckQuery extends Query<Void> {

    private static final Logger log = LoggerFactory.getLogger(IntegrityCheckQuery.class);

    @Override
    public String getMaintenanceMarker() {
        return "integrity_check";
    }

    @Override
    public void prepare(ResourceProvider resources) {
        resources.borrowAllDomainObjects(QueryPool.LockType.EXCLUSIVE);
    }

    @Override //todo it!
    public Void execute(QueryTransaction transaction) throws PlatformException {
        try {
            for (Subsystem module : Subsystems.getInstance().getCluster().getDependencyOrderedComponentsOf(Subsystem.class)) {
                module.buildSchemaService()
                        .setChangeMode(ChangeMode.NONE)
                        .setValidationMode(true)
                        .execute();
            }
            logIntegrityCheck(IntegrityCheckStatus.SUCCESS);
            log.info("Integrity check successes");
        } catch (DatabaseException e) {
            logIntegrityCheck(IntegrityCheckStatus.FAIL);
            log.error("Integrity check failed", e);

            Subsystems.getInstance().getQueryPool().setHardException(GeneralExceptionBuilder.buildDatabaseException(e));
        }
        return null;
    }

    private void logIntegrityCheck(IntegrityCheckStatus status) {
        SecurityLog.info(
                new SyslogStructDataEvent(CoreEvent.Database.TYPE_INTEGRITY_CHECK)
                        .withParam(CoreParameter.Database.STATUS, status.name().toLowerCase()),
                new SyslogStructDataTarget(CoreTarget.TYPE_DATABASE),
                new ContextImpl(new SourceSystemImpl())
        );
    }
}
