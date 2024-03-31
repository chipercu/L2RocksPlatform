package com.fuzzy.subsystem.core.employeeaccess;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessReadable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessReadable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessReadable;
import com.fuzzy.subsystems.entityelements.DomainObjectEntityElementEnumerator;

public class ManagerEmployeeAccessEnumerator
        extends DomainObjectEntityElementEnumerator<
                Long,
                ManagerDepartmentAccessReadable,
                ManagerEmployeeAccessReadable,
                ManagerAllAccessReadable> {

    public ManagerEmployeeAccessEnumerator(ResourceProvider resources) {
        super(
                resources.getReadableResource(ManagerDepartmentAccessReadable.class),
                resources.getReadableResource(ManagerEmployeeAccessReadable.class),
                resources.getReadableResource(ManagerAllAccessReadable.class),
                ManagerDepartmentAccessReadable.FIELD_MANAGER_ID
        );
    }
}
