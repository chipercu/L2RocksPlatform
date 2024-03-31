package com.fuzzy.subsystems.domainobject.config;

import com.fuzzy.database.domainobject.DomainObjectEditable;

public interface ConfigEditable extends DomainObjectEditable {

    void setName(String name);

    void setValue(byte[] value);
}