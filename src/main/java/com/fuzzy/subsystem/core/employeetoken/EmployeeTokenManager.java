package com.fuzzy.subsystem.core.employeetoken;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;

public interface EmployeeTokenManager<T extends DomainObject & DomainObjectEditable & EmployeeTokenEditable> {

    EmployeeTokenReadable createToken(long employeeId, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable removeToken(String token, QueryTransaction transaction) throws PlatformException;

    EmployeeTokenReadable getByToken(String token, QueryTransaction transaction) throws PlatformException;
}
