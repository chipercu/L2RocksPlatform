package com.fuzzy.subsystem.core.graphql.mutation.employee;

import com.fuzzy.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.filterhandler.StandardFilterHandler;
import com.fuzzy.subsystems.graphql.input.GFilterOperation;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeFilterProcessor {

    private final ReadableResource<DepartmentReadable> departmentReadableResource;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final DepartmentGrouping departmentGrouping;
    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

    public EmployeeFilterProcessor(ResourceProvider resources) {
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        departmentGrouping = new DepartmentGrouping(resources);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public Set<Long> getEmployees(Boolean targetAll,
                                  String targetAllParamName,
                                  Set<Long> targetDepartments,
                                  Set<Long> targetEmployees,
                                  ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        if (targetDepartments == null && targetEmployees == null) {
            if (targetAll == null || !targetAll) {
                throw GeneralExceptionBuilder.buildInvalidValueException(targetAllParamName, targetAll);
            }
        } else if (targetAll != null && targetAll) {
            throw GeneralExceptionBuilder.buildInvalidValueException(targetAllParamName, targetAll);
        }
        GStandardFilter standardFilter = null;
        if (targetDepartments != null || targetEmployees != null) {
            HashSet<Long> nodes = null;
            HashSet<Long> items = null;
            PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(true);
            if (targetDepartments != null) {
                nodes = primaryKeyValidator.validate(targetDepartments, departmentReadableResource, transaction);
            }
            if (targetEmployees != null) {
                items = primaryKeyValidator.validate(targetEmployees, employeeReadableResource, transaction);
            }
            standardFilter = new GStandardFilter(GFilterOperation.INCLUDE, nodes, items);
        }
        StandardFilterHandler<EmployeeReadable> filterHandler =
                new StandardFilterHandler<>(employeeReadableResource, departmentGrouping);
        Set<Long> employees = filterHandler.get(standardFilter, transaction);
        UnauthorizedContext authContext = context.getSource().getAuthContext();
        if (authContext instanceof EmployeeAuthContext) {
            long employeeId = ((EmployeeAuthContext)authContext).getEmployeeId();
            ManagerEmployeeAccess access = managerEmployeeAccessGetter.getAccess(employeeId, context.getTransaction());
            employees = employees.stream().filter(access::checkEmployee).collect(Collectors.toSet());

        }
        return employees;
    }
}
