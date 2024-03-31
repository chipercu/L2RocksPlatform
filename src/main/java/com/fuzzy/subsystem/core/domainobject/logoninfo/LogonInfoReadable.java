package com.fuzzy.subsystem.core.domainobject.logoninfo;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.anotation.Field;
import com.fuzzy.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import java.time.Instant;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "LogonInfo",
        fields = {
                @Field(name = "employee_id", number = LogonInfoReadable.FIELD_EMPLOYEE_ID,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "enabled_logon", number = LogonInfoReadable.FIELD_ENABLED_LOGON,
                        type = Boolean.class),
                @Field(name = "last_logon_time", number = LogonInfoReadable.FIELD_LAST_LOGON_TIME,
                        type = Instant.class)
        },
        hashIndexes = {
                @HashIndex(fields = { LogonInfoReadable.FIELD_EMPLOYEE_ID })
        }
)
public class LogonInfoReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    @Deprecated
    public final static int FIELD_ENABLED_LOGON = 1;
    public final static int FIELD_LAST_LOGON_TIME = 2;

    public LogonInfoReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    @Deprecated // Удалить в версии 1.0.17
    public boolean getEnabledLogon() {
        return getBoolean(FIELD_ENABLED_LOGON);
    }

    public Instant getLastLogonTime() {
        return getInstant(FIELD_LAST_LOGON_TIME);
    }
}