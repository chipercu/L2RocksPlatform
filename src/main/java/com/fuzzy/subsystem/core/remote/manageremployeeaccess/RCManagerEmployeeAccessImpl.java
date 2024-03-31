package com.fuzzy.subsystem.core.remote.manageremployeeaccess;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessSetter;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.HashSet;

public class RCManagerEmployeeAccessImpl extends AbstractQueryRController<CoreSubsystem> implements RCManagerEmployeeAccess {

    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private ManagerEmployeeAccessSetter managerEmployeeAccessSetter;

    public RCManagerEmployeeAccessImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        managerEmployeeAccessSetter = new ManagerEmployeeAccessSetter(component, resources);
    }

    @Override
    public void set(long managerId,
                    HashSet<Long> insertedDepartments,
                    HashSet<Long> insertedEmployees,
                    HashSet<Long> removedDepartments,
                    HashSet<Long> removedEmployees,
                    ContextTransaction context) throws PlatformException {
        validateManager(managerId, context.getTransaction());
        managerEmployeeAccessSetter.set(
                managerId,
                insertedDepartments,
                insertedEmployees,
                removedDepartments,
                removedEmployees,
                context.getTransaction()
        );
    }

    @Override
    public void setAll(long managerId, ContextTransaction context) throws PlatformException {
        validateManager(managerId, context.getTransaction());
        managerEmployeeAccessSetter.setAll(managerId, context.getTransaction());
    }

    @Override
    public void setState(long managerId,
                         HashSet<Long> departments,
                         HashSet<Long> employees,
                         ContextTransaction context) throws PlatformException {
        validateManager(managerId, context.getTransaction());
        managerEmployeeAccessSetter.setState(managerId, departments, employees, context.getTransaction());
    }

    private void validateManager(long managerId, QueryTransaction transaction) throws PlatformException {
        new PrimaryKeyValidator(false).validate(managerId, employeeReadableResource, transaction);
    }
}
