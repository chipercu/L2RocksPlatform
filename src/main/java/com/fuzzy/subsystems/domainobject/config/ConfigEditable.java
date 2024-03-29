package com.fuzzy.subsystems.domainobject.config;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public interface ConfigEditable extends DomainObjectEditable {

    void setName(String name);

    void setValue(byte[] value);
}