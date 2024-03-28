package com.fuzzy.subsystems.domainobject.config;

import com.fuzzy.subsystems.remote.RDomainObject;

public class ConfigReadable extends RDomainObject {

    public final static int FIELD_NAME = 0;
    public final static int FIELD_VALUE = 1;

    public ConfigReadable(long id) {
        super(id);
    }

    public String getName() {
        return getString(FIELD_NAME);
    }

    public byte[] getValue() {
        return get(FIELD_VALUE);
    }
}