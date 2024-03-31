package com.fuzzy.subsystem.core.employeetoken;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;

public interface EmployeeTokenManager<T extends DomainObject & DomainObjectEditable & EmployeeTokenEditable> {

    EmployeeTokenReadable createToken(long employeeId, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable removeToken(String token, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable getByToken(String token, QueryTransaction transaction) throws PlatformException;
}
