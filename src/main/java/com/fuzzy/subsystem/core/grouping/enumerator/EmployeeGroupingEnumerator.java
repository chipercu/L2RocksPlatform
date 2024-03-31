package com.fuzzy.subsystem.core.grouping.enumerator;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.grouping.DomainObjectGroupingEnumeratorImpl;

public class EmployeeGroupingEnumerator extends DomainObjectGroupingEnumeratorImpl<EmployeeReadable> {

    public EmployeeGroupingEnumerator(ResourceProvider resources) {
        super(resources, EmployeeReadable.class, EmployeeReadable.FIELD_DEPARTMENT_ID);
    }
}
