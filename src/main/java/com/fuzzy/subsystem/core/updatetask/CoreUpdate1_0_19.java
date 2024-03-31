package com.fuzzy.subsystem.core.updatetask;

import com.fuzzy.database.domainobject.Transaction;
import com.fuzzy.database.exception.DatabaseException;
import com.fuzzy.platform.update.UpdateTask;
import com.fuzzy.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.19.x",
        previousVersion = "1.0.18.x"
)
public class CoreUpdate1_0_19 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_19(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {}
}