package com.fuzzy.subsystem.core.employeetoken;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;

public interface EmployeeTokenManager<T extends DomainObject & DomainObjectEditable & EmployeeTokenEditable> {

    EmployeeTokenReadable createToken(long employeeId, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable removeToken(String token, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable getByToken(String token, QueryTransaction transaction) throws PlatformException;
}
