package com.fuzzy.subsystem.core.autocomplete;

import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystems.autocomplete.PathGetterImpl;

public class DepartmentPathGetter extends PathGetterImpl<DepartmentReadable, DepartmentReadable> {

    public DepartmentPathGetter(ResourceProvider resources) {
        super(
                resources,
                DepartmentReadable.FIELD_PARENT_ID,
                DepartmentReadable.FIELD_NAME,
                new DepartmentGroupingEnumerator(resources),
                DepartmentReadable.class
        );
    }

}
