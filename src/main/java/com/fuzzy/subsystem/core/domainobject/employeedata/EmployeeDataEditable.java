package com.fuzzy.subsystem.core.domainobject.employeedata;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public class EmployeeDataEditable extends EmployeeDataReadable implements DomainObjectEditable {

    public EmployeeDataEditable(long id) {
        super(id);
    }

    public void setEmployeeId(Long value) {
        set(FIELD_EMPLOYEE_ID, value);
    }

    public void setKey(String value) {
        set(FIELD_KEY, value);
    }

    public void setValue(String value) {
        set(FIELD_VALUE, value);
    }
}
