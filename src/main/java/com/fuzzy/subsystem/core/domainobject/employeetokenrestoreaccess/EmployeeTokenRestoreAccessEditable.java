package com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess;

import com.fuzzy.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenEditable;

import java.time.Instant;

public class EmployeeTokenRestoreAccessEditable extends EmployeeTokenRestoreAccessReadable
        implements DomainObjectEditable, EmployeeTokenEditable {

    public EmployeeTokenRestoreAccessEditable(long id) {
        super(id);
    }

    @Override
    public void setEmployeeId(Long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    @Override
    public void setToken(String token) {
        set(FIELD_TOKEN, token);
    }

    @Override
    public void setCreationTime(Instant creationTime) {
        set(FIELD_CREATED, creationTime);
    }
}
