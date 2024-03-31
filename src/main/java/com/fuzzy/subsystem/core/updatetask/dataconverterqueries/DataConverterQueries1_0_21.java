package com.fuzzy.subsystem.core.updatetask.dataconverterqueries;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.EditableResource;
import com.fuzzy.platform.querypool.Query;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.platform.sdk.context.impl.ContextTransactionImpl;
import com.fuzzy.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.CoreConfigSetter;
import com.fuzzy.subsystem.core.domainobject.license.LicenseEditable;

public class DataConverterQueries1_0_21 extends Query<Void> {
    EditableResource<LicenseEditable> licenseEditableResource;
    CoreConfigGetter coreConfigGetter;
    CoreConfigSetter coreConfigSetter;

    @Override
    public void prepare(ResourceProvider resources) throws PlatformException {
        licenseEditableResource = resources.getEditableResource(LicenseEditable.class);
        coreConfigGetter = new CoreConfigGetter(resources);
        coreConfigSetter = new CoreConfigSetter(resources);
    }

    @Override
    public Void execute(QueryTransaction transaction) throws PlatformException {
        byte[] encryptedLicense = coreConfigGetter.get(CoreConfigDescription.LICENSE, transaction);
        if (encryptedLicense != null) {
            LicenseEditable licenseEditable = licenseEditableResource.create(transaction);
            licenseEditable.setLicenseKey(encryptedLicense);
            licenseEditableResource.save(licenseEditable, transaction);
            ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
            coreConfigSetter.set(CoreConfigDescription.LICENSE, null, contextTransaction);
        }
        return null;
    }
}
