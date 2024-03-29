package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.rdao.database.exception.DatabaseException;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.updatetask.dataconverterqueries.DataConverterQueries1_0_23;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.23.x",
        previousVersion = "1.0.22.x"
)
public class CoreUpdate1_0_23 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_23(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {
        removeV1Licenses();
    }

    private void removeV1Licenses() {
        executeQuery(new DataConverterQueries1_0_23());
    }
}
