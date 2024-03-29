package com.fuzzy.subsystem.core.textfilter;

import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Collections;
import java.util.Set;

public class DepartmentTextFilterGetter extends DomainObjectTextFilterGetter<DepartmentReadable> {

    public static final Set<Integer> FIELD_NAMES =
            Collections.unmodifiableSet(Collections.singleton(DepartmentReadable.FIELD_NAME));

    public DepartmentTextFilterGetter(ResourceProvider resources) {
        super(
                resources.getReadableResource(DepartmentReadable.class),
                FIELD_NAMES
        );
    }
}
