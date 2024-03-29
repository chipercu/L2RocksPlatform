package com.fuzzy.subsystem.core.list;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employeeaccessrole.EmployeeAccessRoleReadable;
import com.fuzzy.subsystem.core.textfilter.AccessRoleTextFilterGetter;
import com.fuzzy.subsystems.list.ListBuilder;
import com.fuzzy.subsystems.list.ListChecker;
import com.fuzzy.subsystems.list.ListResult;
import com.fuzzy.subsystems.utils.ComparatorUtility;

import java.util.Set;

public class AccessRoleListBuilder {

    private static class Checker implements ListChecker<AccessRoleReadable> {

        private final ReadableResource<EmployeeAccessRoleReadable> employeeAccessRoleReadableResource;
        private Set<Long> employeeFilter;

        public Checker(ResourceProvider resources) {
            employeeAccessRoleReadableResource = resources.getReadableResource(EmployeeAccessRoleReadable.class);
        }

        public void setEmployeeFilter(Set<Long> employeeFilter) {
            this.employeeFilter = employeeFilter;
        }

        @Override
        public boolean checkItem(AccessRoleReadable item, ContextTransaction<?> context) throws PlatformException {
            if (employeeFilter == null) {
                return true;
            }
            HashFilter filter = new HashFilter(EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID, item.getId());
            try (IteratorEntity<EmployeeAccessRoleReadable> ie =
                         employeeAccessRoleReadableResource.findAll(filter, context.getTransaction())) {
                while (ie.hasNext()) {
                    EmployeeAccessRoleReadable employeeAccessRole = ie.next();
                    if (employeeFilter.contains(employeeAccessRole.getEmployeeId())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private final Checker checker;
    private final ListBuilder<AccessRoleReadable> listBuilder;

    public AccessRoleListBuilder(ResourceProvider resources) {
        checker = new Checker(resources);
        listBuilder = new ListBuilder<>(
                resources.getReadableResource(AccessRoleReadable.class),
                new AccessRoleTextFilterGetter(resources),
                checker
        );
        listBuilder.setComparator((o1, o2) -> ComparatorUtility.compare(o1.getId(), o1.getName(), o2.getId(), o2.getName()));
    }

    public ListResult<AccessRoleReadable> build(AccessRoleListParam param, ContextTransaction<?> context)
            throws PlatformException {
        checker.setEmployeeFilter(param.getEmployeeFilter());
        return listBuilder.build(param, context);
    }
}