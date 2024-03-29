package com.fuzzy.subsystem.core.domainobject.employeedata;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeData",
        fields = {
                @Field(name = "employee_id", number = EmployeeDataReadable.FIELD_EMPLOYEE_ID,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "key", number = EmployeeDataReadable.FIELD_KEY,
                        type = String.class),
                @Field(name = "value", number = EmployeeDataReadable.FIELD_VALUE,
                        type = String.class)
        },
        hashIndexes = {
                @HashIndex(fields = { EmployeeDataReadable.FIELD_EMPLOYEE_ID }),
                @HashIndex(fields = { EmployeeDataReadable.FIELD_EMPLOYEE_ID, EmployeeDataReadable.FIELD_KEY })
        }
)
public class EmployeeDataReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    public final static int FIELD_KEY = 1;
    public final static int FIELD_VALUE = 2;

    public EmployeeDataReadable(long id) {
        super(id);
    }

    public long getEmployeeId() {
        return get(FIELD_EMPLOYEE_ID);
    }

    public String getKey() {
        return get(FIELD_KEY);
    }

    public String getValue() {
        return get(FIELD_VALUE);
    }
}