package com.fuzzy.subsystem.core.domainobject.authentication;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public class AuthenticationEditable extends AuthenticationReadable implements DomainObjectEditable {

    public AuthenticationEditable(long id) {
        super(id);
    }

    public void setName(String name) {
        set(FIELD_NAME, name);
    }

    public void setType(String type) {
        set(FIELD_TYPE, type);
    }
}