package com.fuzzy.subsystems.entityprivilege;

import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.PrivilegeEnum;

public interface EntityPrivilegeReadable<T extends PrivilegeEnum> {

    int FIELD_PRIVILEGE = 0;
    int FIELD_OPERATIONS = 1;

    T getPrivilege();

    AccessOperationCollection getOperations();
}
