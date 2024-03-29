package com.fuzzy.subsystem.core.domainobject.employeeaccessrole;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "EmployeeAccessRole",
        fields = {
                @Field(name = "employee_id", number = EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID,
                        type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "access_role_id", number = EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID,
                        type = Long.class, foreignDependency = AccessRoleReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = { EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID }),
                @HashIndex(fields = { EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID }),
                @HashIndex(fields = {
                        EmployeeAccessRoleReadable.FIELD_EMPLOYEE_ID,
                        EmployeeAccessRoleReadable.FIELD_ACCESS_ROLE_ID
                })
        }
)
public class EmployeeAccessRoleReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    public final static int FIELD_ACCESS_ROLE_ID = 1;

    public EmployeeAccessRoleReadable(long id) {
        super(id);
    }

    public Long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    public Long getAccessRoleId() {
        return getLong(FIELD_ACCESS_ROLE_ID);
    }
}
