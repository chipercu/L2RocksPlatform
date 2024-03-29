package com.fuzzy.subsystem.core.tree.employee;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystems.tree.TreeChecker;

public class EmployeeTreeChecker implements TreeChecker<DepartmentReadable, EmployeeReadable> {

    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private Long authEmployeeId = null;
    private ManagerEmployeeAccess access = null;

    public EmployeeTreeChecker(ResourceProvider resources) {
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    public void setAuthEmployeeId(Long authEmployeeId) {
        this.authEmployeeId = authEmployeeId;
    }

    @Override
    public boolean checkNode(DepartmentReadable departmentReadable, QueryTransaction transaction)
            throws PlatformException {
        return authEmployeeId == null || ensureAccess(transaction).checkDepartment(departmentReadable.getId());
    }

    @Override
    public boolean checkItem(EmployeeReadable employeeReadable, QueryTransaction transaction)
            throws PlatformException {
        return authEmployeeId == null || ensureAccess(transaction).checkEmployee(employeeReadable.getId());
    }

    public ManagerEmployeeAccess ensureAccess(QueryTransaction transaction) throws PlatformException {
        if (access == null) {
            access = managerEmployeeAccessGetter.getAccess(authEmployeeId, transaction);
        }
        return access;
    }
}