package com.fuzzy.subsystem.core.domainobject.employeetokenrestoreaccess;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeetoken.EmployeeTokenReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import java.time.Instant;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeTokenRestoreAccess",
        fields = {
                @Field(name = "employee_id", number = EmployeeTokenRestoreAccessReadable.FIELD_EMPLOYEE_ID, type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "token", number = EmployeeTokenRestoreAccessReadable.FIELD_TOKEN, type = String.class),
                @Field(name = "created", number = EmployeeTokenRestoreAccessReadable.FIELD_CREATED, type = Instant.class)
        },
        hashIndexes = {
                @HashIndex(fields = {EmployeeTokenRestoreAccessReadable.FIELD_EMPLOYEE_ID}),
                @HashIndex(fields = {EmployeeTokenRestoreAccessReadable.FIELD_TOKEN})
        }
)
public class EmployeeTokenRestoreAccessReadable extends RDomainObject implements EmployeeTokenReadable {

    public EmployeeTokenRestoreAccessReadable(long id) {
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
