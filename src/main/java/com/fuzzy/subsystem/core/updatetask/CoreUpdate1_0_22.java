package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.database.domainobject.Transaction;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.database.schema.Schema;
import com.fuzzy.platform.update.UpdateTask;
import com.fuzzy.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.22.x",
        previousVersion = "1.0.21.x"
)
public class CoreUpdate1_0_22 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_22(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        dropTableApplicationTag(schema);
        dropTableApplication(schema);
        dropTableTrack(schema);
    }

    private static void dropTableApplication(Schema schema) {
        schema.dropTable("Application", CoreSubsystemConsts.UUID);
    }

    private static void dropTableApplicationTag(Schema schema) {
        schema.dropTable("ApplicationTag", CoreSubsystemConsts.UUID);
    }

    private static void dropTableTrack(Schema schema) {
        schema.dropTable("Track", CoreSubsystemConsts.UUID);
    }
}
