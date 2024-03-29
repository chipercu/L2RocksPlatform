package com.fuzzy.subsystem.core.employeeaccess;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.managerallaccess.ManagerAllAccessEditable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessEditable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessReadable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessEditable;
import com.fuzzy.subsystem.core.domainobject.manageremployeeaccess.ManagerEmployeeAccessReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.subscription.employee.GEmployeeUpdateEvent;
import com.fuzzy.subsystems.entityelements.EntityElementSetter;

import java.util.Collection;

public class ManagerEmployeeAccessSetter
        extends EntityElementSetter<DepartmentReadable, EmployeeReadable,
                ManagerDepartmentAccessEditable, ManagerEmployeeAccessEditable, ManagerAllAccessEditable> {

    private final CoreSubsystem component;
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final ReadableResource<AccessRoleReadable> accessRoleReadableResource;
    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

    public ManagerEmployeeAccessSetter(CoreSubsystem component, ResourceProvider resources) {
        super(
                resources.getReadableResource(DepartmentReadable.class),
                resources.getReadableResource(EmployeeReadable.class),
                resources.getRemovableResource(ManagerDepartmentAccessEditable.class),
                resources.getRemovableResource(ManagerEmployeeAccessEditable.class),
                resources.getRemovableResource(ManagerAllAccessEditable.class),
                null,
                ManagerDepartmentAccessReadable.FIELD_MANAGER_ID,
                ManagerDepartmentAccessReadable.FIELD_DEPARTMENT_ID,
                ManagerEmployeeAccessReadable.FIELD_EMPLOYEE_ID
        );
        this.component = component;
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        accessRoleReadableResource = resources.getReadableResource(AccessRoleReadable.class);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    @Override
    public void set(
            final Long managerId,
            final Collection<Long> insertedDepartments,
            final Collection<Long> insertedEmployees,
            final Collection<Long> removedDepartments,
            final Collection<Long> removedEmployees,
            QueryTransaction transaction
    ) throws PlatformException {
        sendUpdateFullAccessEvent(managerId, false, transaction);
        HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID, managerId);
        try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                     employeeAccessRoleReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                long accessRoleId = ie.next().getAccessRoleId();
                AccessRoleReadable accessRole = accessRoleReadableResource.get(accessRoleId, transaction);
                if (accessRole.isAdmin()) {
                    throw CoreExceptionBuilder.buildRequireAllEmployeeAccessException();
                }
            }
        }
        super.set(managerId, insertedDepartments, insertedEmployees, removedDepartments, removedEmployees, transaction);
    }

    @Override
    public void setAll(Long managerId, QueryTransaction transaction) throws PlatformException {
        sendUpdateFullAccessEvent(managerId, true, transaction);
        super.setAll(managerId, transaction);
    }

    @Override
    public void setState(Long managerId, Collection<Long> departments, Collection<Long> employees, QueryTransaction transaction) throws PlatformException {
        sendUpdateFullAccessEvent(managerId, false, transaction);
        super.setState(managerId, departments, employees, transaction);
    }

    private void sendUpdateFullAccessEvent(long managerId, boolean newFullAccessValue, QueryTransaction transaction) throws PlatformException {
        boolean prevFullAccessValue = managerEmployeeAccessGetter.getAccess(managerId, transaction).isAll();
        if (prevFullAccessValue != newFullAccessValue) {
            GEmployeeUpdateEvent.send(component, managerId, transaction);
        }
    }
}
