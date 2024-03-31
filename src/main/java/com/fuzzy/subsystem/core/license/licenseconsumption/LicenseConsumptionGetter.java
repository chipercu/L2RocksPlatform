package com.fuzzy.subsystem.core.license.licenseconsumption;

import com.fuzzy.database.domainobject.filter.EmptyFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;

import java.util.HashSet;
import java.util.Set;

public class LicenseConsumptionGetter {
    private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;

    public LicenseConsumptionGetter(ResourceProvider resources) {
        employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
    }

    public long getUsersWithRole(ContextTransaction context) throws PlatformException {
        Set<Long> employees = new HashSet<>();
        try (IteratorEntity<EmployeeAccessRoleReadable> ie = employeeAccessRoleReadableResource.findAll(EmptyFilter.INSTANCE, context.getTransaction())) {
            while (ie.hasNext()) {
                EmployeeAccessRoleReadable employeeAccessRoleReadable = ie.next();
                employees.add(employeeAccessRoleReadable.getEmployeeId());
            }
        }
        return employees.size();
    }
}
