package com.fuzzy.platform.sdk.domainobject.module;

import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.platform.sdk.component.version.Version;
import com.fuzzy.platform.sdk.domainobject.module.ModuleReadable;

public class ModuleEditable extends ModuleReadable implements DomainObjectEditable {

    public ModuleEditable(long id) {
        super(id);
    }

    public void setUuid(String value) {
        set(FIELD_UUID, value);
    }

    public void setVersion(Version value) {
        set(FIELD_VERSION, value.toString());
    }
}
