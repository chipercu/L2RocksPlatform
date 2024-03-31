package com.fuzzy.subsystem.core.domainobject.accessroleprivilege;

import com.fuzzy.database.anotation.Entity;
import com.fuzzy.database.anotation.Field;
import com.fuzzy.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "AccessRoleCorePrivilege",
        fields = {
                @Field(name = "access_role_id", number = AccessRoleCorePrivilegeReadable.FIELD_ACCESS_ROLE_ID,
                        type = Long.class, foreignDependency = AccessRoleReadable.class),
                @Field(name = "privilege", number = AccessRoleCorePrivilegeReadable.FIELD_PRIVILEGE,
                        type = Integer.class),
                @Field(name = "operations", number = AccessRoleCorePrivilegeReadable.FIELD_OPERATIONS,
                        type = Integer.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AccessRoleCorePrivilegeReadable.FIELD_ACCESS_ROLE_ID})
        }
)
public class AccessRoleCorePrivilegeReadable extends RDomainObject implements EntityPrivilegeReadable<CorePrivilege> {

    public static final int FIELD_ACCESS_ROLE_ID = EntityPrivilegeReadable.FIELD_OPERATIONS + 1;

    public AccessRoleCorePrivilegeReadable(long id) {
        super(id);
    }

    public Long getAccessRoleId() {
        return getLong(FIELD_ACCESS_ROLE_ID);
    }

    public CorePrivilege getPrivilege() {
        return CorePrivilege.valueOf(getInteger(FIELD_PRIVILEGE));
    }

    public AccessOperationCollection getOperations() {
        return new AccessOperationCollection(getInteger(FIELD_OPERATIONS));
    }
}
