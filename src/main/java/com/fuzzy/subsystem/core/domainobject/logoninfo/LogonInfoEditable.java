package com.fuzzy.subsystem.core.domainobject.logoninfo;

import com.fuzzy.main.rdao.database.domainobject.DomainObjectEditable;

import java.time.Instant;

public class LogonInfoEditable extends LogonInfoReadable implements DomainObjectEditable {

    public LogonInfoEditable(long id) {
        super(id);
    }

    public void setEmployeeId(long employeeId) {
        set(FIELD_EMPLOYEE_ID, employeeId);
    }

    public void setLastLogonTime(Instant time) {
        set(FIELD_LAST_LOGON_TIME, time);
    }
}