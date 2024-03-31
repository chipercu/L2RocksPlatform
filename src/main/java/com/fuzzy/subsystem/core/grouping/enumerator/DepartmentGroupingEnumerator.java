package com.fuzzy.subsystem.core.grouping.enumerator;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystems.grouping.DomainObjectGroupingEnumeratorImpl;

public class DepartmentGroupingEnumerator extends DomainObjectGroupingEnumeratorImpl<DepartmentReadable> {

    public DepartmentGroupingEnumerator(ResourceProvider resources) {
        super(resources, DepartmentReadable.class, DepartmentReadable.FIELD_PARENT_ID);
    }
}
