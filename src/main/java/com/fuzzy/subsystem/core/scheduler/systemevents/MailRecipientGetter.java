package com.fuzzy.subsystem.core.scheduler.systemevents;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.access.AccessOperation;

import java.util.ArrayList;
import java.util.Arrays;

public class MailRecipientGetter {

    private final EmployeePrivilegesGetter employeePrivilegesGetter;
    private final ReadableResource<EmployeeReadable> employeeReadableResource;

    public MailRecipientGetter(ResourceProvider resources) {
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
    }

    public ArrayList<EmployeeReadable> getRecipients(QueryTransaction transaction) throws PlatformException {
        ArrayList<EmployeeReadable> employees = new ArrayList<>();
        final HashFilter filter = new HashFilter(EmployeeReadable.FIELD_SEND_SYSTEM_EVENTS, Boolean.TRUE);
        try (IteratorEntity<EmployeeReadable> iterator
                     = employeeReadableResource.findAll(filter, transaction)) {
            while (iterator.hasNext()) {
                final EmployeeReadable employeeReadable = iterator.next();
                if (isContainAccess(employeeReadable, transaction)
                        && isContainEmail(employeeReadable)) {
                    employees.add(employeeReadable);
                }
            }
        }
        return employees;
    }

    private boolean isContainAccess(EmployeeReadable employeeReadable, QueryTransaction transaction) throws PlatformException {
        return employeePrivilegesGetter.checkPrivilegeAccessOperations(
                employeeReadable.getId(),
                CorePrivilege.EMPLOYEE_ACCESS.getUniqueKey(),
                Arrays.asList(AccessOperation.WRITE, AccessOperation.EXECUTE),
                new ContextTransactionImpl(new SourceSystemImpl(), transaction));
    }

    private boolean isContainEmail(EmployeeReadable employeeReadable) {
        return employeeReadable.getEmail() != null;
    }
}