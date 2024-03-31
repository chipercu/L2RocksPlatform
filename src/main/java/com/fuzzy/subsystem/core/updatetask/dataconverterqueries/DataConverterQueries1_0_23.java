package com.fuzzy.subsystem.core.updatetask.dataconverterqueries;

import com.fuzzy.main.Subsystems;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.RemovableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.license.LicenseEditable;
import com.fuzzy.subsystem.core.license.LicenseManager;
import com.fuzzy.subsystem.core.remote.crypto.RCCrypto;

import java.util.HashSet;
import java.util.Set;

public class DataConverterQueries1_0_23 extends Query<Void> {

    private RemovableResource<LicenseEditable> licenseRemovableResource;
    private RCCrypto rcCrypto;
    private LicenseManager licenseManager;

    @Override
    public void prepare(ResourceProvider resources) {
        licenseRemovableResource = resources.getRemovableResource(LicenseEditable.class);
        rcCrypto = resources.getQueryRemoteController(CoreSubsystem.class, RCCrypto.class);
        CoreSubsystem coreSubsystem = Subsystems.getInstance().getCluster().getAnyLocalComponent(CoreSubsystem.class);
        licenseManager = new LicenseManager(coreSubsystem, resources);
    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
        Set<LicenseEditable> invalidLicenses = new HashSet<>();
        licenseRemovableResource.forEach(licenseEditable -> {
            String signedLicense = rcCrypto.decryptAsString(licenseEditable.getLicenseKey(), contextTransaction);
            if (signedLicense != null) {
                try {
                    licenseManager.decryptLicense(signedLicense);
                } catch (PlatformException e) {
                    invalidLicenses.add(licenseEditable);
                }
            }
        }, transaction);
        for (LicenseEditable licenseEditable : invalidLicenses) {
            licenseRemovableResource.remove(licenseEditable, transaction);
        }
        return null;
    }
}
