package com.fuzzy.subsystem.core.domainobject.employeeauthorizationhistory;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.anotation.Field;
import com.fuzzy.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import java.time.Instant;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeAuthorizationHistory",
        fields = {
                @Field(name = "employee_id",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_EMPLOYEE_ID,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "last_password_change_utc_time",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_LAST_PASSWORD_CHANGE_UTC_TIME,
                        type = Instant.class),
                @Field(name = "last_logon_utc_time",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_LAST_LOGON_UTC_TIME,
                        type = Instant.class),
                @Field(name = "last_ip_address",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_LAST_IP_ADDRESS,
                        type = String.class),
                @Field(name = "invalid_logon_count",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_INVALID_LOGON_COUNT,
                        type = Integer.class),
                @Field(name = "last_invalid_logon_utc_time",
                        number = EmployeeAuthorizationHistoryReadable.FIELD_LAST_INVALID_LOGON_UTC_TIME,
                        type = Instant.class),
        },
        hashIndexes = {
                @HashIndex(fields = {EmployeeAuthorizationHistoryReadable.FIELD_EMPLOYEE_ID})
        }
)
public class EmployeeAuthorizationHistoryReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    public final static int FIELD_LAST_PASSWORD_CHANGE_UTC_TIME = 1;
    public final static int FIELD_LAST_LOGON_UTC_TIME = 2;
    public final static int FIELD_LAST_IP_ADDRESS = 3;
    public final static int FIELD_INVALID_LOGON_COUNT = 4;
    public final static int FIELD_LAST_INVALID_LOGON_UTC_TIME = 5;

    public EmployeeAuthorizationHistoryReadable(long id) {
        super(id);
    }

    public Long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    public Instant getLastPasswordChangeUtcTime() {
        return getInstant(FIELD_LAST_PASSWORD_CHANGE_UTC_TIME);
    }

    public Instant getLastLogonUtcTime() {
        return getInstant(FIELD_LAST_LOGON_UTC_TIME);
    }

    public String getLastIpAddress() {
        return getString(FIELD_LAST_IP_ADDRESS);
    }

    public Integer getInvalidLogonCount() {
        return getInteger(FIELD_INVALID_LOGON_COUNT);
    }

    public Instant getLastInvalidLogonUtcTime() {
        return getInstant(FIELD_LAST_INVALID_LOGON_UTC_TIME);
    }
}
