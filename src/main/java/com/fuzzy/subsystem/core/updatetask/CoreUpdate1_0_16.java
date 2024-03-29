package com.fuzzy.subsystem.core.updatetask;

import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;

@Update(
        componentUUID = CoreSubsystemConsts.UUID,
        version = "1.0.16.x",
        previousVersion = "1.0.15.x"
)
public class CoreUpdate1_0_16 extends UpdateTask<CoreSubsystem> {

    public CoreUpdate1_0_16(CoreSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) throws DatabaseException {

    }
}
