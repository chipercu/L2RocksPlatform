package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;
import com.fuzzy.subsystems.remote.RCExecutor;

public class RCLicenseBusinessRoleCheckerImpl extends AbstractQueryRController<CoreSubsystem> implements RCLicenseBusinessRoleChecker {
    private final RCExecutor<RCLicenseBusinessAdminChecker> licenseBusinessAdminCheckerRCExecutor;
    private final RCExecutor<RCLicenseBusinessAnalystChecker> licenseBusinessAnalystCheckerRCExecutor;
    private final RCExecutor<RCLicenseBusinessUserChecker> licenseBusinessUserCheckerRCExecutor;

    public RCLicenseBusinessRoleCheckerImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        licenseBusinessAdminCheckerRCExecutor = new RCExecutor<>(resources, RCLicenseBusinessAdminChecker.class);
        licenseBusinessAnalystCheckerRCExecutor = new RCExecutor<>(resources, RCLicenseBusinessAnalystChecker.class);
        licenseBusinessUserCheckerRCExecutor = new RCExecutor<>(resources, RCLicenseBusinessUserChecker.class);
    }

    public BusinessRoleLimit getAccessRoleBusinessRole(Long accessRoleId, ContextTransaction context) throws PlatformException {
        for (BusinessRoleLimit businessRole : BusinessRoleLimit.values()) {
            if (isAccessRoleMatchesBusinessRole(accessRoleId, businessRole, context)) {
                return businessRole;
            }
        }
        return null;
    }

    public BusinessRoleLimit getEmployeeBusinessRole(Long employeeId, ContextTransaction context) throws PlatformException {
        for (BusinessRoleLimit businessRole : BusinessRoleLimit.values()) {
            if (isEmployeeMatchesBusinessRole(employeeId, businessRole, context)) {
                return businessRole;
            }
        }
        return null;
    }

    public boolean isAccessRoleMatchesBusinessRole(Long accessRoleId, BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException {
        switch (businessRoleLimit) {
            case ADMIN -> {
                return licenseBusinessAdminCheckerRCExecutor.isExistTrueResult(rc -> rc.isBusinessAdminRole(accessRoleId, context));
            }
            case ANALYST -> {
                return licenseBusinessAnalystCheckerRCExecutor.isExistTrueResult(rc -> rc.isBusinessAnalystRole(accessRoleId, context));
            }
        }
        return false;
    }

    public boolean isEmployeeMatchesBusinessRole(Long employeeId, BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException {
        switch (businessRoleLimit) {
            case ADMIN -> {
                return licenseBusinessAdminCheckerRCExecutor.isExistTrueResult(rc -> rc.isBusinessAdminEmployee(employeeId, context));
            }
            case ANALYST -> {
                return licenseBusinessAnalystCheckerRCExecutor.isExistTrueResult(rc -> rc.isBusinessAnalystEmployee(employeeId, context));
            }
            case BUSINESS_USER -> {
                return licenseBusinessUserCheckerRCExecutor.isExistTrueResult(rc -> rc.isBusinessUserEmployee(employeeId, context));
            }
        }
        return false;
    }
}
