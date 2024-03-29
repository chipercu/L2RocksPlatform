package com.fuzzy.subsystem.core.domainobject.employeeauthentication;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeAuthentication",
        fields = {
                @Field(name = "employee_id", number = EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "authentication_id", number = EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID,
                        type = Long.class, foreignDependency = AuthenticationReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = { EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID }),
                @HashIndex(fields = { EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID }),
                @HashIndex(fields = {
                        EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID,
                        EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID
                })
        }
)
public class EmployeeAuthenticationReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    public final static int FIELD_AUTHENTICATION_ID = 1;

    public EmployeeAuthenticationReadable(long id) {
        super(id);
    }

    public long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    public long getAuthenticationId() {
        return getLong(FIELD_AUTHENTICATION_ID);
    }
}