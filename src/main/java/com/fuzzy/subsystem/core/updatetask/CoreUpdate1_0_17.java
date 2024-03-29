package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.table.TField;
import com.fuzzy.main.rdao.database.schema.table.THashIndex;
import com.fuzzy.main.rdao.database.schema.table.Table;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

import java.util.List;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.17.x",
        previousVersion = "1.0.16.x"
)
//    -upgrade {"update":[{"uuid":"com.infomaximum.subsystem.core","old_version":"1.0.16","new_version":"1.0.17"}, {"uuid":"com.infomaximum.subsystem.frontend","old_version":"1.0.16","new_version":"1.0.17"}]}
public class CoreUpdate1_0_17 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_17(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        updateEmployeeTable(schema);
        createEmployeeSystemNotification(schema);
    }

    private void updateEmployeeTable(Schema schema) {
        String employeeTableName = "Employee";
        schema.dropField("manual_time_zone_id", employeeTableName, CoreSubsystemConsts.UUID);
        schema.dropField("auto_time_zone_id", employeeTableName, CoreSubsystemConsts.UUID);
        schema.dropField("is_auto_time_zone", employeeTableName, CoreSubsystemConsts.UUID);
    }

    private void createEmployeeSystemNotification(Schema schema) {

        final TField employeeIdField = new TField("employee_id", Long.class);
        final TField messageHashField = new TField("message_hash", Integer.class);

        List<TField> fields = List.of(
                employeeIdField,
                messageHashField
        );

        List<THashIndex> hashIndexes = List.of(
                new THashIndex(employeeIdField),
                new THashIndex(messageHashField),
                new THashIndex(employeeIdField, messageHashField)
        );

        schema.createTable(new Table(
                "EmployeeSystemNotification",
                CoreSubsystemConsts.UUID,
                fields,
                hashIndexes)
        );
    }
}