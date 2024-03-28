package com.fuzzy.subsystems.entityprivilege;

import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.PrivilegeEnum;

public class PrivilegeOperationsPair<T extends PrivilegeEnum> {
    private T privilege;
    private AccessOperationCollection operations;

    public PrivilegeOperationsPair(T privilege, AccessOperationCollection operations) {
        this.privilege = privilege;
        this.operations = operations;
    }

    public T getPrivilege() {
        return privilege;
    }

    public AccessOperationCollection getOperations() {
        return new AccessOperationCollection(operations.getValue() & privilege.getAvailableOperations().getValue());
    }
}
