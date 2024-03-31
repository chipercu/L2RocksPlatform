package com.fuzzy.subsystems.access;

import com.fuzzy.database.utils.BaseEnum;

public interface PrivilegeEnum extends BaseEnum {

    String getUniqueKey();

    AccessOperationCollection getAvailableOperations();
}
