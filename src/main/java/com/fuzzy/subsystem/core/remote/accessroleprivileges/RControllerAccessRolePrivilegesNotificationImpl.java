package com.fuzzy.subsystem.core.remote.accessroleprivileges;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystem.core.remote.licensebusinessrolechecker.RCLicenseBusinessRoleChecker;
import com.fuzzy.subsystem.core.remote.licensedemployee.RCLicensedEmployeeGetter;

public class RControllerAccessRolePrivilegesNotificationImpl extends AbstractQueryRController<CoreSubsystem> implements RControllerAccessRolePrivilegesNotification {

    private final AccessRolePrivilegesSecurityLogger accessRolePrivilegesSecurityLogger;
    private final RCLicenseBusinessRoleChecker rcLicenseBusinessRoleChecker;
    private final RCLicensedEmployeeGetter licensedEmployeeGetter;
    private BusinessRoleLimit oldBusinessRole = null;

    public RControllerAccessRolePrivilegesNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        accessRolePrivilegesSecurityLogger = new AccessRolePrivilegesSecurityLogger(resources);
        rcLicenseBusinessRoleChecker = resources.getQueryRemoteController(CoreSubsystem.class, RCLicenseBusinessRoleChecker.class);
        licensedEmployeeGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCLicensedEmployeeGetter.class);
    }

    @Override
    public void onBeforeChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException {
        accessRolePrivilegesSecurityLogger.saveStateBeforeModifications(accessRoleId, context);
        oldBusinessRole = rcLicenseBusinessRoleChecker.getAccessRoleBusinessRole(accessRoleId, context);
    }

    @Override
    public void onAfterChangePrivileges(long accessRoleId, ContextTransaction context) throws PlatformException {
        BusinessRoleLimit newBusinessRole = rcLicenseBusinessRoleChecker.getAccessRoleBusinessRole(accessRoleId, context);
        if (newBusinessRole != null) {
            if (!newBusinessRole.equals(oldBusinessRole)) {
                licensedEmployeeGetter.validateLicensedEmployeesCount(newBusinessRole, context);
            }
        }
        accessRolePrivilegesSecurityLogger.writeToLog(context);
    }
}
