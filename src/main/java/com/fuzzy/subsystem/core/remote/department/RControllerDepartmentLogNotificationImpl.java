package com.fuzzy.subsystem.core.remote.department;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.securitylog.ManagerEmployeeAccessSecurityLogger;

public class RControllerDepartmentLogNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerDepartmentLogNotification {

    private ManagerEmployeeAccessSecurityLogger managerEmployeeAccessSecurityLogger;

    public RControllerDepartmentLogNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        managerEmployeeAccessSecurityLogger = new ManagerEmployeeAccessSecurityLogger(resources);
    }

    @Override
    public void startChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException {
        managerEmployeeAccessSecurityLogger.beforeMoveDepartment(departmentId, context);
    }

    @Override
    public void endChangeParentDepartment(Long departmentId, ContextTransaction context)
            throws PlatformException {
        managerEmployeeAccessSecurityLogger.afterMoveDepartment(departmentId, context);
    }
}
