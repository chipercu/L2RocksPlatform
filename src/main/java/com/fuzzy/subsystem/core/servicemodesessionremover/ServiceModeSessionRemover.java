package com.fuzzy.subsystem.core.servicemodesessionremover;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.accessroleprivileges.EmployeePrivilegesGetter;
import com.fuzzy.subsystem.frontend.FrontendSubsystem;
import com.fuzzy.subsystems.access.AccessOperationCollection;

import java.util.HashMap;

public class ServiceModeSessionRemover {

    private final EmployeePrivilegesGetter employeePrivilegesGetter;

    public ServiceModeSessionRemover(ResourceProvider resources) {
        employeePrivilegesGetter = new EmployeePrivilegesGetter(resources);
    }

    public void clearSessionForEmployeeIfNeed(FrontendSubsystem frontendSubsystem,
                                               long employeeId,
                                               ContextTransaction<?> context) throws PlatformException {
        HashMap<String, AccessOperationCollection> privileges =
                employeePrivilegesGetter.getPrivileges(employeeId, context);
        AccessOperationCollection operations = privileges.get(CorePrivilege.SERVICE_MODE.getUniqueKey());
        if (operations != null && !operations.isEmpty()) {
            return;
        }
        context.getTransaction().addCommitListener(() -> frontendSubsystem.getSessionService().clearSessions(employeeId, context));
    }
}
