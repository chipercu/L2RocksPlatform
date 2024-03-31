package com.fuzzy.subsystems.accesscscheme.queries.accessschemeemployeelist;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccess;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EmployeeAccessValidator {

    private final ManagerEmployeeAccessGetter managerEmployeeAccessGetter;
    private ManagerEmployeeAccess access;

    public EmployeeAccessValidator(@NonNull ResourceProvider resources) {
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
        access = null;
    }

    public boolean checkEmployee(long employeeId, @NonNull ContextTransactionRequest context) throws PlatformException {
        if (access == null) {
            access = managerEmployeeAccessGetter.getAccess(context);
        }
        return access.checkEmployee(employeeId);
    }
}
