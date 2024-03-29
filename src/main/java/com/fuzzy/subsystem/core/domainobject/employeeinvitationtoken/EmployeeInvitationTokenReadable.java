package com.fuzzy.subsystem.core.domainobject.employeeinvitationtoken;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import java.time.Instant;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeInvitationToken",
        fields = {
                @Field(name = "employee_id", number = EmployeeInvitationTokenReadable.FIELD_EMPLOYEE_ID, type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "token", number = EmployeeInvitationTokenReadable.FIELD_TOKEN, type = String.class),
                @Field(name = "created", number = EmployeeInvitationTokenReadable.FIELD_CREATED, type = Instant.class)
        },
        hashIndexes = {
                @HashIndex(fields = {EmployeeInvitationTokenReadable.FIELD_EMPLOYEE_ID}),
                @HashIndex(fields = {EmployeeInvitationTokenReadable.FIELD_TOKEN})
        }
)
public class EmployeeInvitationTokenReadable extends RDomainObject implements EmployeeTokenReadable {

    public EmployeeInvitationTokenReadable(long id) {
        super(id);
    }

    @Override
    public Long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    @Override
    public String getToken() {
        return getString(FIELD_TOKEN);
    }

    @Override
    public Instant getCreationTime() {
        return getInstant(FIELD_CREATED);
    }
}