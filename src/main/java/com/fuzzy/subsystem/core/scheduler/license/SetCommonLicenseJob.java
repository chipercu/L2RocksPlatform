package com.fuzzy.subsystem.core.scheduler.license;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.remote.licensenotification.RCLicenseNotification;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.scheduler.Job;

public class SetCommonLicenseJob extends Job {
    LicenseManager licenseManager;
    CoreSubsystem component;
    private RCExecutor<RCLicenseNotification> rCLicenseNotificationsExecutor;

    public SetCommonLicenseJob(CoreSubsystem component) {
        this.component = component;
    }

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        licenseManager = new LicenseManager(component, resources);
        rCLicenseNotificationsExecutor = new RCExecutor<>(resources, RCLicenseNotification.class);
    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        ContextTransaction<?> context = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
        rCLicenseNotificationsExecutor.exec(rcLicenseNotification -> rcLicenseNotification.onBeforeExpirationLicense(context));
        licenseManager.actualizeCommonLicense(context);
        return null;
    }
}
