package com.fuzzy.subsystem.core.graphql.query.department;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.grouping.DepartmentGrouping;

import java.util.HashSet;
import java.util.Set;

class DepartmentVisibleGetter {

    private final DepartmentGrouping departmentGrouping;
    private final ManagerEmployeeAccessGetter accessGetter;

    public DepartmentVisibleGetter(ResourceProvider resources) {
        departmentGrouping = new DepartmentGrouping(resources);
        accessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public Set<Long> getVisibleDepartments(ContextTransactionRequest context) throws PlatformException {
        ManagerEmployeeAccess access = accessGetter.getAccess(context);
        if (access.isAll()) {
            return null;
        }
        QueryTransaction transaction = context.getTransaction();
        Set<Long> visibleDepartments = new HashSet<>(access.getElements().nodes);
        for (Long departmentId : access.getElements().nodes) {
            departmentGrouping.forEachParentOfNodeRecursively(departmentId, transaction, visibleDepartments::add);
        }
        for (Long employeeId : access.getElements().items) {
            departmentGrouping.forEachParentOfItemRecursively(employeeId, transaction, visibleDepartments::add);
        }
        return visibleDepartments;
    }
}
