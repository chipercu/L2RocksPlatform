package com.fuzzy.subsystem.core.remote.licensebusinessrolechecker;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;

public interface RCLicenseBusinessRoleChecker extends QueryRemoteController {

    BusinessRoleLimit getAccessRoleBusinessRole(Long accessRoleId, ContextTransaction context) throws PlatformException;

    BusinessRoleLimit getEmployeeBusinessRole(Long employeeId, ContextTransaction context) throws PlatformException;

    boolean isAccessRoleMatchesBusinessRole(Long accessRoleId, BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;

    boolean isEmployeeMatchesBusinessRole(Long employeeId, BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;
}
