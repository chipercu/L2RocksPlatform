package com.fuzzy.subsystem.core.domainobject.employeeaccessrole;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

public class EmployeeAccessRoleEditable extends EmployeeAccessRoleReadable implements DomainObjectEditable {

    public EmployeeAccessRoleEditable(long id) {
        super(id);
    }

    public void setEmployeeId(long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    public void setAccessRoleId(Long accessRoleId) {
        set(FIELD_ACCESS_ROLE_ID, accessRoleId);
    }
}
