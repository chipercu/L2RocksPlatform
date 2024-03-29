package com.fuzzy.subsystem.core.scheduler.license;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.main.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.license.enums.LicenseParameter;
import com.fuzzy.subsystem.core.remote.liscense.RCLicenseParameterReset;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.scheduler.Job;

public class ResetLicenseParameterCurrentStateJob extends Job {

    private final LicenseParameter licenseParameter;
    private RCExecutor<RCLicenseParameterReset> executor;

    public ResetLicenseParameterCurrentStateJob(LicenseParameter licenseParameter) {
        this.licenseParameter = licenseParameter;
    }

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        executor = new RCExecutor<>(resources, RCLicenseParameterReset.class);
    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        ContextTransaction<?> context = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
        executor.exec(rc -> rc.resetLicenseParameterCurrentState(rc.getComponentUuid(), licenseParameter, context));
        return null;
    }
}
