package com.fuzzy.subsystem.core.domainobject.employeeauthentication;

import com.fuzzy.database.domainobject.DomainObjectEditable;

public class EmployeeAuthenticationEditable extends EmployeeAuthenticationReadable implements DomainObjectEditable {

    public EmployeeAuthenticationEditable(long id) {
        super(id);
    }

    public void setEmployeeId(long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    public void setAuthenticationId(long authenticationId) {
        set(FIELD_AUTHENTICATION_ID, authenticationId);
    }
}