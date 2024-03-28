package com.fuzzy.subsystems.entityprivilege;

import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.access.PrivilegeEnum;

public interface EntityPrivilegeEditable<T extends PrivilegeEnum> extends EntityPrivilegeReadable<T> {

    void setPrivilege(T privilege);

    void setOperations(AccessOperationCollection operations);


}
