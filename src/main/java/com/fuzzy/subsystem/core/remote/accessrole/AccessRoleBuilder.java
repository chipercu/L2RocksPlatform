package com.fuzzy.subsystem.core.remote.accessrole;

import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystems.modelspace.BuilderFields;

public class AccessRoleBuilder extends BuilderFields {

    public AccessRoleBuilder withName(String name) {
        fields.put(AccessRoleReadable.FIELD_NAME, name);
        return this;
    }
    public AccessRoleBuilder withAdmin(boolean admin) {
        fields.put(AccessRoleReadable.FIELD_ADMIN, admin);
        return this;
    }

    public boolean isContainName() {
        return fields.containsKey(AccessRoleReadable.FIELD_NAME);
    }
    public boolean isContainAdmin() {
        return fields.containsKey(AccessRoleReadable.FIELD_ADMIN);
    }

    public String getName() {
        return (String) fields.get(AccessRoleReadable.FIELD_NAME);
    }
    public Boolean isAdmin() {
        return (Boolean) fields.get(AccessRoleReadable.FIELD_ADMIN);
    }
}
