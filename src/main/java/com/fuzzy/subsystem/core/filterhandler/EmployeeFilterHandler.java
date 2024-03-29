package com.fuzzy.subsystem.core.filterhandler;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystem.core.textfilter.EmployeeTextFilterGetter;
import com.fuzzy.subsystems.filterhandler.StandardFilterHandler;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.input.GTextFilter;

import java.util.HashSet;

public class EmployeeFilterHandler {

    private final StandardFilterHandler<EmployeeReadable> employeeStandardFilterHandler;
    private final EmployeeTextFilterGetter employeeTextFilterGetter;

    public EmployeeFilterHandler(ResourceProvider resources) {
        employeeStandardFilterHandler = new StandardFilterHandler <>(
                resources.getReadableResource(EmployeeReadable.class),
                new DepartmentGrouping(resources)
        );
        employeeTextFilterGetter = new EmployeeTextFilterGetter(resources);
    }

    public HashSet<Long> get(GStandardFilter employeeFilter, QueryTransaction transaction) throws PlatformException {
        return employeeStandardFilterHandler.get(employeeFilter, transaction);
    }

    public HashSet<Long> get(GStandardFilter employeeFilter,
                             GTextFilter textFilter,
                             QueryTransaction transaction) throws PlatformException {
        HashSet<Long> employees = get(employeeFilter, transaction);
        if (textFilter != null && textFilter.isSpecified()) {
            HashSet<Long> textFilterEmployees = new HashSet<>();
            try(IteratorEntity<EmployeeReadable> ie =
                        employeeTextFilterGetter.findAll(textFilter.getText(), transaction)) {
                while (ie.hasNext()) {
                    long employeeId = ie.next().getId();
                    if (employees.contains(employeeId)) {
                        textFilterEmployees.add(employeeId);
                    }
                }
            }
            employees = textFilterEmployees;
        }
        return employees;
    }
}
