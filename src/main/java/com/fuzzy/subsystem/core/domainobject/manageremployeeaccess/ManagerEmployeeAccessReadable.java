package com.fuzzy.subsystem.core.domainobject.manageremployeeaccess;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.entityelements.EntityElementReadable;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "ManagerEmployeeAccess",
        fields = {
                @Field(name = "manager_id", number = ManagerEmployeeAccessReadable.FIELD_MANAGER_ID, type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "employee_id", number = ManagerEmployeeAccessReadable.FIELD_EMPLOYEE_ID, type = Long.class, foreignDependency = EmployeeReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = {ManagerEmployeeAccessReadable.FIELD_MANAGER_ID}),
                @HashIndex(fields = {ManagerEmployeeAccessReadable.FIELD_MANAGER_ID, ManagerEmployeeAccessReadable.FIELD_EMPLOYEE_ID})
        }
)
public class ManagerEmployeeAccessReadable extends EntityElementReadable<Long> {

    public final static int FIELD_MANAGER_ID = 0;
    public final static int FIELD_EMPLOYEE_ID = 1;

    public ManagerEmployeeAccessReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public Long getManagerId() {
        return getLong(FIELD_MANAGER_ID);
    }

    public Long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    @Override
    public Long getEntityId() {
        return getManagerId();
    }

    @Override
    public Long getElement() {
        return getEmployeeId();
    }
}
