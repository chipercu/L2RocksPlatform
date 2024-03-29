package com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.entityelements.EntityElementReadable;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "ManagerDepartmentAccess",
        fields = {
                @Field(name = "manager_id", number = ManagerDepartmentAccessReadable.FIELD_MANAGER_ID, type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "department_id", number = ManagerDepartmentAccessReadable.FIELD_DEPARTMENT_ID, type = Long.class, foreignDependency = DepartmentReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = {ManagerDepartmentAccessReadable.FIELD_MANAGER_ID}),
                @HashIndex(fields = {ManagerDepartmentAccessReadable.FIELD_MANAGER_ID, ManagerDepartmentAccessReadable.FIELD_DEPARTMENT_ID})
        }
)
public class ManagerDepartmentAccessReadable extends EntityElementReadable<Long> {

    public final static int FIELD_MANAGER_ID = 0;
    public final static int FIELD_DEPARTMENT_ID = 1;

    public ManagerDepartmentAccessReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public Long getManagerId() {
        return getLong(FIELD_MANAGER_ID);
    }

    public Long getDepartmentId() {
        return getLong(FIELD_DEPARTMENT_ID);
    }

    @Override
    public Long getEntityId() {
        return getManagerId();
    }

    @Override
    public Long getElement() {
        return getDepartmentId();
    }
}
