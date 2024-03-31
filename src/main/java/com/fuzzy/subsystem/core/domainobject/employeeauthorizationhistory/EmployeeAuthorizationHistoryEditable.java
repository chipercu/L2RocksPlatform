package com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory;

import com.fuzzy.database.domainobject.DomainObjectEditable;

import java.time.Instant;

public class EmployeeAuthorizationHistoryEditable extends EmployeeAuthorizationHistoryReadable implements DomainObjectEditable {

    public EmployeeAuthorizationHistoryEditable(long id) {
        super(id);
    }

    public void setEmployeeId(Long value) {
        set(FIELD_EMPLOYEE_ID, value);
    }

    public void setLastPasswordChangeUtcTime(Instant value) {
        set(FIELD_LAST_PASSWORD_CHANGE_UTC_TIME, value);
    }

    public void setLastLogonUtcTime(Instant value) {
        set(FIELD_LAST_LOGON_UTC_TIME, value);
    }

    public void setLastIpAddress(String value) {
        set(FIELD_LAST_IP_ADDRESS, value);
    }

    public void setInvalidLogonCount(Integer value) {
        set(FIELD_INVALID_LOGON_COUNT, value);
    }

    public void setLastInvalidLogonUtcTime(Instant value) {
        set(FIELD_LAST_INVALID_LOGON_UTC_TIME, value);
    }
}
