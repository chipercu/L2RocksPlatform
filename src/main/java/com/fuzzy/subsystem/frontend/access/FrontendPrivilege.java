package com.fuzzy.subsystem.frontend.access;

import com.fuzzy.subsystem.core.remote.accessroleprivileges.PrivilegeValue;
import com.fuzzy.subsystems.access.AccessOperation;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.Privilege;
import com.fuzzy.subsystems.access.PrivilegeEnum;

public enum FrontendPrivilege implements PrivilegeEnum {

    // Привилегия "Доступ к документации"
    DOCUMENTATION_ACCESS(1, "frontend_privilege.documentation_access", AccessOperation.READ);

    private final Privilege privilege;

    FrontendPrivilege(int id, String key, AccessOperation... availableOperations) {
        this.privilege = new Privilege(id, key, availableOperations);
    }

    @Override
    public String getUniqueKey() {
        return privilege.getUniqueKey();
    }

    @Override
    public AccessOperationCollection getAvailableOperations() {
        return privilege.getAvailableOperations();
    }

    @Override
    public int intValue() {
        return privilege.intValue();
    }

    public static FrontendPrivilege valueOf(int value) {
        for (FrontendPrivilege privilege : FrontendPrivilege.values()) {
            if (privilege.intValue() == value) {
                return privilege;
            }
        }
        return null;
    }

    public static FrontendPrivilege ofKey(String key) {
        for (FrontendPrivilege privilege : FrontendPrivilege.values()) {
            if (privilege.getUniqueKey().equals(key)) {
                return privilege;
            }
        }
        return null;
    }

    public static PrivilegeValue[] getDocumentationAccessPrivileges() {
        PrivilegeValue[] privilegeValues = new PrivilegeValue[FrontendPrivilege.values().length];
        int i = 0;
        for (FrontendPrivilege privilege : FrontendPrivilege.values()) {
            privilegeValues[i++] = new PrivilegeValue(privilege.getUniqueKey(), privilege.getAvailableOperations());
        }
        return privilegeValues;
    }
}
