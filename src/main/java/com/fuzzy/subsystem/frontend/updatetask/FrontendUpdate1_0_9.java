package com.fuzzy.subsystem.frontend.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.FrontendSubsystemConsts;

@Update(
        componentUUID = FrontendSubsystemConsts.UUID,
        version = "1.0.9.x",
        previousVersion = "1.0.8.x"
)
public class FrontendUpdate1_0_9 extends UpdateTask<FrontendSubsystem> {

    public FrontendUpdate1_0_9(FrontendSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) {

    }
}