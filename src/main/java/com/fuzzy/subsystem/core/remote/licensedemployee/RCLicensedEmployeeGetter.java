package com.fuzzy.subsystem.core.remote.licensedemployee;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;

import java.util.HashSet;

public interface RCLicensedEmployeeGetter extends QueryRemoteController {
   HashSet<Long> getLicensedEmployees(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;

   void validateLicensedEmployeesCount(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;
}
