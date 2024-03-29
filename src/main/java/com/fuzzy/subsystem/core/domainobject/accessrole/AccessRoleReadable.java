package com.fuzzy.subsystem.core.domainobject.accessrole;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.infomaximum.database.anotation.PrefixIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "AccessRole",
        fields = {
                @Field(name = "name", number = AccessRoleReadable.FIELD_NAME, type = String.class),
                @Field(name = "admin", number = AccessRoleReadable.FIELD_ADMIN, type = Boolean.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AccessRoleReadable.FIELD_NAME }),
                @HashIndex(fields = { AccessRoleReadable.FIELD_ADMIN })
        },
        prefixIndexes = {
                @PrefixIndex(fields = { AccessRoleReadable.FIELD_NAME })
        }
)
public class AccessRoleReadable extends RDomainObject {

    public final static int FIELD_NAME = 0;
    public final static int FIELD_ADMIN = 1;

    public AccessRoleReadable(long id) {
        super(id);
    }

    public String getName() {
        return getString(FIELD_NAME);
    }

    public boolean isAdmin() {
        Boolean admin = getBoolean(FIELD_ADMIN);
        return admin != null ? admin : false;
    }
}
