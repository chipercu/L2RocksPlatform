package com.fuzzy.subsystem.core.domainobject.managerallaccess;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.entityelements.EntityReadable;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "ManagerAllAccess",
        fields = {
                @Field(name = "manager_id", number = ManagerAllAccessReadable.FIELD_MANAGER_ID, type = Long.class, foreignDependency = EmployeeReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = {ManagerAllAccessReadable.FIELD_MANAGER_ID})
        }
)
public class ManagerAllAccessReadable extends EntityReadable {

    public final static int FIELD_MANAGER_ID = 0;

    public ManagerAllAccessReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public Long getManagerId() {
        return getLong(FIELD_MANAGER_ID);
    }

    @Override
    public Long getEntityId() {
        return getManagerId();
    }
}
