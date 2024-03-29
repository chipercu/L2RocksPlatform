package com.fuzzy.subsystem.core.filterhandler;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.input.GTextFilter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AccessibleEmployeeFilterHandler {

    private final EmployeeFilterHandler employeeFilterHandler;
    private final ManagerEmployeeAccessGetter accessGetter;

    public AccessibleEmployeeFilterHandler(ResourceProvider resources) {
        employeeFilterHandler = new EmployeeFilterHandler(resources);
        accessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public HashSet<Long> get(GStandardFilter employeeFilter, ContextTransactionRequest context) throws PlatformException {
        return getAccessibleEmployees(employeeFilterHandler.get(employeeFilter, context.getTransaction()), context);
    }

    public HashSet<Long> get(GStandardFilter employeeFilter,
                             GTextFilter textFilter,
                             ContextTransactionRequest context) throws PlatformException {
        return getAccessibleEmployees(employeeFilterHandler.get(employeeFilter, textFilter, context.getTransaction()), context);
    }

    private HashSet<Long> getAccessibleEmployees(Set<Long> employees,
                                                 ContextTransactionRequest context) throws PlatformException {
        ManagerEmployeeAccess access = accessGetter.getAccess(context);
        return employees.stream().filter(access::checkEmployee).collect(Collectors.toCollection(HashSet::new));
    }
}