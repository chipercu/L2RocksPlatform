package com.fuzzy.main.platform.sdk.domainobject.module;

import com.fuzzy.main.platform.sdk.component.version.Version;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

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
