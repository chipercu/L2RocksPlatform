package com.fuzzy.subsystem.core.domainobject.accessrole;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class AccessRoleEditable extends AccessRoleReadable implements DomainObjectEditable {

    public AccessRoleEditable(long id) {
        super(id);
    }

    public void setName(String name) {
        set(FIELD_NAME, name);
    }

    public void setAdmin(boolean admin) {
        set(FIELD_ADMIN, admin);
    }
}
