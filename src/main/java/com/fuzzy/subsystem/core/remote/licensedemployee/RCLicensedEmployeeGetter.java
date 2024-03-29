package com.fuzzy.subsystem.core.remote.licensedemployee;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryRemoteController;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.license.enums.BusinessRoleLimit;

import java.util.HashSet;

public interface RCLicensedEmployeeGetter extends QueryRemoteController {
   HashSet<Long> getLicensedEmployees(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;

   void validateLicensedEmployeesCount(BusinessRoleLimit businessRoleLimit, ContextTransaction context) throws PlatformException;
}
