package com.fuzzy.subsystem.core.domainobject.usedpassword;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "UsedPassword",
        fields = {
                @Field(name = "employee_id", number = UsedPasswordReadable.FIELD_EMPLOYEE_ID, type = Long.class, foreignDependency = EmployeeReadable.class),
                @Field(name = "salty_password_hash", number = UsedPasswordReadable.FIELD_SALTY_PASSWORD_HASH, type = byte[].class),
                @Field(name = "salt", number = UsedPasswordReadable.FIELD_SALT, type = byte[].class)
        },
        hashIndexes = {
                @HashIndex(fields = {UsedPasswordReadable.FIELD_EMPLOYEE_ID}),
        }
)
public class UsedPasswordReadable extends RDomainObject {

    public final static int FIELD_EMPLOYEE_ID = 0;
    public final static int FIELD_SALTY_PASSWORD_HASH = 1;
    public final static int FIELD_SALT = 2;

    public UsedPasswordReadable(long id) {
        super(id);
    }

    @Override
    public long getId() {
        return super.getId();
    }

    public Long getEmployeeId() {
        return getLong(FIELD_EMPLOYEE_ID);
    }

    public byte[] getSaltyPasswordHash() {
        return get(FIELD_SALTY_PASSWORD_HASH);
    }

    public byte[] getSalt() {
        return get(FIELD_SALT);
    }
}