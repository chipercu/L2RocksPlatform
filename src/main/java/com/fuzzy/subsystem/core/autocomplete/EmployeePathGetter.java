package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystems.autocomplete.PathGetterImpl;

public class EmployeePathGetter extends PathGetterImpl<EmployeeReadable, DepartmentReadable> {

    public EmployeePathGetter(ResourceProvider resources) {
        super(
                resources,
                EmployeeReadable.FIELD_DEPARTMENT_ID,
                DepartmentReadable.FIELD_NAME,
                new DepartmentGroupingEnumerator(resources),
                DepartmentReadable.class
        );
    }

}
