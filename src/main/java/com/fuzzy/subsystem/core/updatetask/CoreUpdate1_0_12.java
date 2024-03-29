package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.Record;
import com.infomaximum.database.RecordIterator;
import com.infomaximum.database.RecordSource;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.12.x",
        previousVersion = "1.0.11.x"
)
public class CoreUpdate1_0_12 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_12(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        updateEmployeeTable(schema);
        schema.dropTable("Position", CoreSubsystemConsts.UUID);
        RecordSource recordSource = new RecordSource(schema.getDbProvider());
        removePositionPrivilege(recordSource, "AccessRoleCorePrivilege");
        removePositionPrivilege(recordSource, "ApiKeyCorePrivilege");
    }

    private void updateEmployeeTable(Schema schema) {
        String tableName = "Employee";
        schema.dropField("enabled_logon", tableName, CoreSubsystemConsts.UUID);
        schema.dropField("position_id", tableName, CoreSubsystemConsts.UUID);
        schema.dropField("last_logon_time", tableName, CoreSubsystemConsts.UUID);
    }

    private void removePositionPrivilege(RecordSource recordSource, String tableName) {
        List<Long> ids = new ArrayList<>();
        try (RecordIterator iterator = recordSource.select(tableName, CoreSubsystemConsts.UUID)) {
            while (iterator.hasNext()) {
                Record record = iterator.next();
                if (Objects.equals(record.getValues()[0], 7)) {
                    ids.add(record.getId());
                }
            }
        }
        try {
            recordSource.executeTransactional(dataCommand -> {
                for (Long id : ids) {
                    dataCommand.deleteRecord(tableName, CoreSubsystemConsts.UUID, id);
                }
            });
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }
}