package com.fuzzy.subsystem.core.domainobject.license;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "License",
        fields = {
                @Field(name = "license_key", number = LicenseReadable.FIELD_LICENSE_KEY,
                        type = byte[].class),
        }
)

public class LicenseReadable extends RDomainObject {

    public final static int FIELD_LICENSE_KEY = 0;

    public LicenseReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public byte[] getLicenseKey() {
        return get(FIELD_LICENSE_KEY);
    }

}
