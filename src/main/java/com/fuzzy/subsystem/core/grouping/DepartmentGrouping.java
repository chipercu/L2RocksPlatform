package com.fuzzy.subsystem.core.grouping;

import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.grouping.enumerator.DepartmentGroupingEnumerator;
import com.fuzzy.subsystem.core.grouping.enumerator.EmployeeGroupingEnumerator;
import com.fuzzy.subsystems.grouping.NodeItemGrouping;

public class DepartmentGrouping extends NodeItemGrouping {

    public DepartmentGrouping(ResourceProvider resources) {
        super(new DepartmentGroupingEnumerator(resources), new EmployeeGroupingEnumerator(resources));
    }
}
