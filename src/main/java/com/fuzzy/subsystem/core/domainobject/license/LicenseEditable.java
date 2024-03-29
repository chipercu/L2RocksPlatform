package com.fuzzy.subsystem.core.domainobject.license;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class LicenseEditable extends LicenseReadable implements DomainObjectEditable {

    public LicenseEditable(long id) {
        super(id);
    }

    public void setLicenseKey(byte[] licenseKey) {
        set(FIELD_LICENSE_KEY, licenseKey);
    }

}
