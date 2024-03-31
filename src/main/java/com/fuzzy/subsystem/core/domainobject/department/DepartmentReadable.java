package com.fuzzy.subsystem.core.domainobject.department;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.anotation.Field;
import com.fuzzy.database.anotation.HashIndex;
import com.fuzzy.database.anotation.PrefixIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

/**
 * Created by kris on 26.04.17.
 */
@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "Department",
        fields = {
                @Field(name = "name", number = DepartmentReadable.FIELD_NAME, type = String.class),
                @Field(name = "parent_id", number = DepartmentReadable.FIELD_PARENT_ID, type = Long.class, foreignDependency = DepartmentReadable.class)
        },
        hashIndexes = {
                @HashIndex(fields = {DepartmentReadable.FIELD_PARENT_ID})
        },
        prefixIndexes = {
                @PrefixIndex(fields = {DepartmentReadable.FIELD_NAME})
        }
)
public class DepartmentReadable extends RDomainObject {

    public final static int FIELD_NAME = 0;
    public final static int FIELD_PARENT_ID = 1;

    public DepartmentReadable(long id) {
        super(id);
    }

    public String getName() {
        return getString(FIELD_NAME);
    }

    public Long getParentDepartmentId() {
        return getLong(FIELD_PARENT_ID);
    }
}
