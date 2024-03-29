package com.fuzzy.subsystem.core.domainobject.license;

import com.fuzzy.subsystems.modelspace.BuilderFields;

public class LicenseBuilder extends BuilderFields {

    public LicenseBuilder withLicenseKey(String value) {
        with(LicenseReadable.FIELD_LICENSE_KEY, value);
        return this;
    }

    public boolean isContainLicenseKey() {
        return isContain(LicenseReadable.FIELD_LICENSE_KEY);
    }

    public String getLicenseKey() {
        return (String) get(LicenseReadable.FIELD_LICENSE_KEY);
    }

}