package com.fuzzy.subsystem.core.domainobject.usedpassword;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class UsedPasswordEditable extends UsedPasswordReadable implements DomainObjectEditable {

    public UsedPasswordEditable(long id) {
        super(id);
    }

    public void setEmployeeId(Long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    public void setSaltyPasswordHash(byte[] saltyPasswordHash) {
        set(FIELD_SALTY_PASSWORD_HASH, saltyPasswordHash);
    }

    public void setSalt(byte[] salt) {
        set(FIELD_SALT, salt);
    }
}