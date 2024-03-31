package com.fuzzy.subsystem.core.employeeaccess;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessEditable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessEditable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessEditable;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.entityelements.DomainObjectEntityElementRecalculator;

public class ManagerEmployeeAccessRecalculator extends
        DomainObjectEntityElementRecalculator<
                        ManagerDepartmentAccessEditable,
                        ManagerEmployeeAccessEditable,
                        ManagerAllAccessEditable> {

    public ManagerEmployeeAccessRecalculator(ResourceProvider resources) {
        super(
                resources.getRemovableResource(ManagerDepartmentAccessEditable.class),
                resources.getRemovableResource(ManagerEmployeeAccessEditable.class),
                resources.getEditableResource(ManagerAllAccessEditable.class),
                new DepartmentGrouping(resources),
                ManagerDepartmentAccessEditable.FIELD_MANAGER_ID,
                ManagerDepartmentAccessEditable.FIELD_DEPARTMENT_ID,
                ManagerEmployeeAccessEditable.FIELD_EMPLOYEE_ID
        );
    }
}
