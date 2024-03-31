package com.fuzzy.subsystem.core.remote.licensedemployee;

import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryRemoteController;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;

import java.util.HashSet;

public interface RCLicensedEmployeeGetter extends QueryRemoteController {
   HashSet<Long> getLicensedEmployees(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;

   void validateLicensedEmployeesCount(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;
}
