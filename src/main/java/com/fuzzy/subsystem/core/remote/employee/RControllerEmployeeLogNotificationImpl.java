package com.fuzzy.subsystem.core.remote.employee;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.securitylog.EmployeeAccessRoleSecurityLogger;
import com.fuzzy.subsystem.core.securitylog.ManagerEmployeeAccessSecurityLogger;

import java.util.HashSet;

public class RControllerEmployeeLogNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerEmployeeLogNotification {

    private ManagerEmployeeAccessSecurityLogger managerEmployeeAccessSecurityLogger;
    private EmployeeAccessRoleSecurityLogger employeeAccessRoleSecurityLogger;

    public RControllerEmployeeLogNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        managerEmployeeAccessSecurityLogger = new ManagerEmployeeAccessSecurityLogger(resources);
        employeeAccessRoleSecurityLogger = new EmployeeAccessRoleSecurityLogger(resources);
    }

    @Override
    public void startCreateEmployee(ContextTransaction context) {
    }

    @Override
    public void endCreateEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        managerEmployeeAccessSecurityLogger.afterCreateEmployee(employeeId, context);
    }

    @Override
    public void startRemoveEmployee(Long employeeId, ContextTransaction context) throws PlatformException {
        managerEmployeeAccessSecurityLogger.beforeRemoveEmployee(employeeId, context);
    }

    @Override
    public void endRemoveEmployee(Long employeeId, ContextTransaction context) {
    }

    @Override
    public void startChangeParentDepartment(Long employeeId, ContextTransaction context) throws PlatformException {
        managerEmployeeAccessSecurityLogger.beforeMoveEmployee(employeeId, context);
    }

    @Override
    public void endChangeParentDepartment(Long employeeId, ContextTransaction context) throws PlatformException {
        managerEmployeeAccessSecurityLogger.afterMoveEmployee(employeeId, context);
    }

    @Override
    public void startMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context)
            throws PlatformException {
        managerEmployeeAccessSecurityLogger.beforeEmployeesMerge(mainEmployeeId, secondaryEmployees, context);
        employeeAccessRoleSecurityLogger.beforeEmployeeAccessRolesModifications(mainEmployeeId, context);
    }

    @Override
    public void endMergeEmployees(long mainEmployeeId, HashSet<Long> secondaryEmployees, ContextTransaction context)
            throws PlatformException {
        managerEmployeeAccessSecurityLogger.afterEmployeesMerge(mainEmployeeId, secondaryEmployees, context);
        employeeAccessRoleSecurityLogger.afterEmployeeAccessRolesModifications(mainEmployeeId, context);
    }
}

