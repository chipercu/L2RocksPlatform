package com.fuzzy.subsystem.frontend.updatetask;

import com.fuzzy.main.rdao.database.domainobject.Transaction;
import com.fuzzy.main.platform.update.UpdateTask;
import com.fuzzy.main.platform.update.annotation.Update;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystem.frontend.FrontendSubsystemConsts;

@Update(
        componentUUID = FrontendSubsystemConsts.UUID,
        version = "1.0.24.x",
        previousVersion = "1.0.23.x"
)
public class FrontendUpdate1_0_24 extends UpdateTask<FrontendSubsystem> {

    public FrontendUpdate1_0_24(FrontendSubsystem subsystem) {
        super(subsystem);
    }

    @Override
    protected void updateComponent(Transaction transaction) {

    }
}