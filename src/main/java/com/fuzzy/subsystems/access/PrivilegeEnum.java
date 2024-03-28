package com.fuzzy.subsystems.access;

import com.fuzzy.main.rdao.database.utils.BaseEnum;

public interface PrivilegeEnum extends BaseEnum {

    String getUniqueKey();

    AccessOperationCollection getAvailableOperations();
}
