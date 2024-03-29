package com.fuzzy.subsystem.core.utils;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.usedpassword.UsedPasswordReadable;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;

import java.util.Arrays;

public class EmployeePasswordSecurityChecker {

    private final ReadableResource<UsedPasswordReadable> usedPasswordReadableResource;

    public EmployeePasswordSecurityChecker(ResourceProvider resources) {
        usedPasswordReadableResource = resources.getReadableResource(UsedPasswordReadable.class);
    }

    public void checkUsedPasswords(EmployeeReadable employee, String newPasswordHash, QueryTransaction transaction)
            throws PlatformException {
        if (employee.checkPasswordHash(newPasswordHash)) {
            throw CoreExceptionBuilder.buildNewPasswordEqualsUsedPasswordException();
        }
        HashFilter filter = new HashFilter(UsedPasswordReadable.FIELD_EMPLOYEE_ID, employee.getId());
        try (IteratorEntity<UsedPasswordReadable> ie =
                     usedPasswordReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                UsedPasswordReadable usedPassword = ie.next();
                byte[] newSaltyPasswordHash =
                        EmployeeReadable.getSaltyPasswordHash(newPasswordHash, usedPassword.getSalt());
                if (Arrays.equals(newSaltyPasswordHash, usedPassword.getSaltyPasswordHash())) {
                    throw CoreExceptionBuilder.buildNewPasswordEqualsUsedPasswordException();
                }
            }
        }
    }
}
