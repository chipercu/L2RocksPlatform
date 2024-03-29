package com.fuzzy.subsystem.core.textfilter;

import com.google.common.collect.ImmutableSet;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.textfilter.DomainObjectTextFilterGetter;

import java.util.Set;

public class EmployeeTextFilterGetter extends DomainObjectTextFilterGetter<EmployeeReadable> {

    public static final Set<Integer> fieldNames = ImmutableSet.of(
            EmployeeReadable.FIELD_FIRST_NAME,
            EmployeeReadable.FIELD_SECOND_NAME,
            EmployeeReadable.FIELD_PATRONYMIC
    );

    public EmployeeTextFilterGetter(ResourceProvider resources) {
        super(
                resources.getReadableResource(EmployeeReadable.class),
                fieldNames
        );
    }
}
