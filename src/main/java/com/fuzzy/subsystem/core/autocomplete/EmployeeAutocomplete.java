package com.fuzzy.subsystem.core.autocomplete;

import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.autocomplete.ExcludeChildrenAutocomplete;

public class EmployeeAutocomplete extends ExcludeChildrenAutocomplete<DepartmentReadable, EmployeeReadable> {

    private final DepartmentAtomicAutocomplete procesetDepartmentAtomicAutocomplete;
    private final EmployeeAtomicAutocomplete employeeAtomicAutocomplete;

    private EmployeeAutocomplete(
            DepartmentAtomicAutocomplete procesetDepartmentAtomicAutocomplete,
            EmployeeAtomicAutocomplete procesetEmployeeAtomicAutocomplete,
            DepartmentGrouping departmentGrouping
    ) {
        super(
                procesetDepartmentAtomicAutocomplete,
                procesetEmployeeAtomicAutocomplete,
                departmentGrouping
        );
        this.procesetDepartmentAtomicAutocomplete = procesetDepartmentAtomicAutocomplete;
        this.employeeAtomicAutocomplete = procesetEmployeeAtomicAutocomplete;
    }

    public EmployeeAutocomplete(ResourceProvider resources) {
        this(
                new DepartmentAtomicAutocomplete(resources),
                new EmployeeAtomicAutocomplete(resources),
                new DepartmentGrouping(resources)
        );
    }

    public void setAuthEmployeeId(Long authEmployeeId) {
        procesetDepartmentAtomicAutocomplete.setAuthEmployeeId(authEmployeeId);
        employeeAtomicAutocomplete.setAuthEmployeeId(authEmployeeId);
    }
}
