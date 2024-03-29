package com.fuzzy.subsystem.core.domainobject.config;

import com.fuzzy.subsystems.domainobject.config.ConfigEditable;

public class CoreConfigEditable extends CoreConfigReadable implements ConfigEditable {

    public CoreConfigEditable(long id) {
        super(id);
    }

    @Override
    public void setName(String name) {
        set(FIELD_NAME, name);
    }

    @Override
    public void setValue(byte[] value) {
        set(FIELD_VALUE, value);
    }
}
