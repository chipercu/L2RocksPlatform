package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.rdao.database.schema.Schema;
import com.fuzzy.main.rdao.database.schema.table.THashIndex;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.13.x",
        previousVersion = "1.0.12.x"
)
public class CoreUpdate1_0_13 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_13(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        Schema schema = getSchema(transaction);
        updateApplicationTable(schema);
    }

    private void updateApplicationTable(Schema schema) {
        THashIndex nameHashIndex = new THashIndex("name");
        schema.createIndex(nameHashIndex, "Application", CoreSubsystemConsts.UUID);
    }
}