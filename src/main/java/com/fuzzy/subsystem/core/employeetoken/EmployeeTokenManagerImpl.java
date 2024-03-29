package com.fuzzy.subsystem.core.employeetoken;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.RemovableResource;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

class EmployeeTokenManagerImpl <T extends DomainObject & DomainObjectEditable & EmployeeTokenEditable>
        implements EmployeeTokenManager<T> {

    private final RemovableResource<T> employeeTokenResource;
    private final Duration tokenExpiredPeriod;

    EmployeeTokenManagerImpl(RemovableResource<T> employeeTokenEditableResource, Duration tokenExpiredPeriod) {
        this.employeeTokenResource = employeeTokenEditableResource;
        this.tokenExpiredPeriod = tokenExpiredPeriod;
    }

    @Override
    public EmployeeTokenReadable createToken(long employeeId, QueryTransaction transaction) throws PlatformException {
        T employeeToken = employeeTokenResource.find(new HashFilter(T.FIELD_EMPLOYEE_ID, employeeId), transaction);
        if (employeeToken == null) {
            employeeToken = employeeTokenResource.create(transaction);
            employeeToken.setEmployeeId(employeeId);
        }
        employeeToken.setToken(generateToken(transaction));
        employeeToken.setCreationTime(Instant.now());
        employeeTokenResource.save(employeeToken, transaction);
        return employeeToken;
    }

    @Override
    public EmployeeTokenReadable removeToken(String token, QueryTransaction transaction) throws PlatformException {
        T employeeToken = find(token, transaction);
        if (employeeToken != null) {
            employeeTokenResource.remove(employeeToken, transaction);
        }
        return employeeToken;
    }

    @Override
    public EmployeeTokenReadable getByToken(String token, QueryTransaction transaction) throws PlatformException {
        return find(token, transaction);
    }

    private T find(String token, QueryTransaction transaction) throws PlatformException {
        T employeeToken = employeeTokenResource.find(new HashFilter(T.FIELD_TOKEN, token), transaction);
        if (employeeToken != null) {
            employeeToken = invalidate(employeeToken, transaction);
        }
        return employeeToken;
    }

    private String generateToken(QueryTransaction transaction) throws PlatformException {
        String token;
        do {
            token = UUID.randomUUID().toString().replace("-", "");
        } while (employeeTokenResource.find(new HashFilter(T.FIELD_TOKEN, token), transaction) != null);

        return token;
    }

    private T invalidate(T employeeToken, QueryTransaction transaction) throws PlatformException {
        Instant instant = Instant.now().minus(tokenExpiredPeriod);
        if (employeeToken.getCreationTime().isBefore(instant)) {
            employeeTokenResource.remove(employeeToken, transaction);
            return null;
        }
        return employeeToken;
    }
}
