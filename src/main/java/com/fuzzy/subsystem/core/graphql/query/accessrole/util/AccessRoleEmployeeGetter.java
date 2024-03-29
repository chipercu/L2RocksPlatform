package com.fuzzy.subsystem.core.graphql.query.accessrole.util;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployeeCollection;
import com.fuzzy.subsystem.core.graphql.query.employee.util.EmployeeCollectionGetter;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;

public class AccessRoleEmployeeGetter {

    private static class EmployeeGetter implements AutoCloseable {

        IteratorEntity<EmployeeAccessRoleReadable> ie;

        public EmployeeGetter(
                long accessRoleId,
                ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource,
                QueryTransaction transaction
        ) throws PlatformException {
            HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, accessRoleId);
            ie = employeeAccessRoleReadableResource.findAll(filter, transaction);
        }

        public Long getEmployeeId() throws PlatformException {
            return ie.hasNext() ? ie.next().getEmployeeId() : null;
        }

        @Override
        public void close() throws PlatformException {
            ie.close();
        }
    }

    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
    private final EmployeeCollectionGetter employeeCollectionGetter;

    public AccessRoleEmployeeGetter(ResourceProvider resources) {
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        employeeCollectionGetter = new EmployeeCollectionGetter(resources);
    }

    public GEmployeeCollection getEmployees(
            long accessRoleId,
            GStandardFilter employeeFilter,
            Integer limit,
            ContextTransactionRequest context
    ) throws PlatformException {
        try (EmployeeGetter employeeGetter =
                     new EmployeeGetter(accessRoleId, employeeAccessRoleReadableResource, context.getTransaction())) {
            return employeeCollectionGetter.getEmployees(employeeGetter::getEmployeeId, employeeFilter, limit, context);
        }
    }

    public int getEmployeeCount(
            long accessRoleId,
            GStandardFilter employeeFilter,
            ContextTransactionRequest context
    ) throws PlatformException {
        try (EmployeeGetter employeeGetter =
                     new EmployeeGetter(accessRoleId, employeeAccessRoleReadableResource, context.getTransaction())) {
            return employeeCollectionGetter.getEmployeeCount(employeeGetter::getEmployeeId, employeeFilter, context);
        }
    }
}
