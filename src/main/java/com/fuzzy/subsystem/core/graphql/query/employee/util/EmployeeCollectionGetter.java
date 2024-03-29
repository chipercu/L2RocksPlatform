package com.fuzzy.subsystem.core.graphql.query.employee.util;

import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.function.Consumer;
import com.fuzzy.subsystem.core.config.CoreConfigDescription;
import com.fuzzy.subsystem.core.config.CoreConfigGetter;
import com.fuzzy.subsystem.core.config.DisplayNameFormat;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.filterhandler.EmployeeFilterHandler;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployeeCollection;
import com.fuzzy.subsystem.core.utils.EmployeeComparator;
import com.fuzzy.subsystems.function.Supplier;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.sorter.Sorter;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.Set;

public class EmployeeCollectionGetter {

    private final ReadableResource<DepartmentReadable> departmentReadableResource;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final EmployeeFilterHandler employeeFilterHandler;
    private final CoreConfigGetter coreConfigGetter;
    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

    public EmployeeCollectionGetter(ResourceProvider resources) {
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeeFilterHandler = new EmployeeFilterHandler(resources);
        coreConfigGetter = new CoreConfigGetter(resources);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public GEmployeeCollection getEmployees(
            Supplier<Long> employeeGetter,
            GStandardFilter employeeFilter,
            Integer limit,
            ContextTransactionRequest context
    ) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        DisplayNameFormat displayNameFormat =
                coreConfigGetter.get(CoreConfigDescription.DISPLAY_NAME_FORMAT, transaction);
        Sorter<EmployeeReadable> sorter = new Sorter<>(new EmployeeComparator(displayNameFormat), limit);
        enumerateEmployees(
                employeeId -> sorter.add(employeeReadableResource.get(employeeId, transaction)),
                employeeGetter,
                employeeFilter,
                context
        );
        return new GEmployeeCollection(sorter);
    }

    public int getEmployeeCount(
            Supplier<Long> employeeGetter,
            GStandardFilter employeeFilter,
            ContextTransactionRequest context
    ) throws PlatformException {
        int[] employeeCount = new int[]{0};
        enumerateEmployees(employeeId -> employeeCount[0]++, employeeGetter, employeeFilter, context);
        return employeeCount[0];
    }

    private void enumerateEmployees(
            Consumer<Long> consumer,
            Supplier<Long> employeeGetter,
            GStandardFilter employeeFilter,
            ContextTransactionRequest context
    ) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Set<Long> employees = null;
        if (employeeFilter != null) {
            PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(true);
            GStandardFilter checkedEmployeeFilter = primaryKeyValidator.validate(
                    employeeFilter,
                    departmentReadableResource,
                    employeeReadableResource,
                    transaction
            );
            employees = employeeFilterHandler.get(checkedEmployeeFilter, transaction);
        }
        ManagerEmployeeAccess employeeAccess = managerEmployeeAccessGetter.getAccess(context);
        Long employeeId;
        while ((employeeId = employeeGetter.get()) != null) {
            if ((employees == null || employees.contains(employeeId)) &&
                    employeeAccess.checkEmployee(employeeId)) {
                consumer.accept(employeeId);
            }
        }
    }
}
