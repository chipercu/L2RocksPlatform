package com.fuzzy.subsystem.core.utils;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
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
