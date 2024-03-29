package com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.frontend.FrontendSubsystemConsts;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = FrontendSubsystemConsts.UUID,
        name = "AccessRoleFrontendPrivilege",
        fields = {
                @Field(name = "access_role_id", number = AccessRoleFrontendPrivilegeReadable.FIELD_ACCESS_ROLE_ID,
                        type = Long.class, foreignDependency = AccessRoleReadable.class),
                @Field(name = "privilege", number = AccessRoleFrontendPrivilegeReadable.FIELD_PRIVILEGE,
                        type = Integer.class),
                @Field(name = "operations", number = AccessRoleFrontendPrivilegeReadable.FIELD_OPERATIONS,
                        type = Integer.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AccessRoleFrontendPrivilegeReadable.FIELD_ACCESS_ROLE_ID})
        }
)
public class AccessRoleFrontendPrivilegeReadable extends RDomainObject implements EntityPrivilegeReadable<FrontendPrivilege> {

    public static final int FIELD_ACCESS_ROLE_ID = EntityPrivilegeReadable.FIELD_OPERATIONS + 1;

    public AccessRoleFrontendPrivilegeReadable(long id) {
        super(id);
    }

    public Long getAccessRoleId() {
        return getLong(FIELD_ACCESS_ROLE_ID);
    }

    @Override
    public FrontendPrivilege getPrivilege() {
        return FrontendPrivilege.valueOf(getInteger(FIELD_PRIVILEGE));
    }

    @Override
    public AccessOperationCollection getOperations() {
        return new AccessOperationCollection(getInteger(FIELD_OPERATIONS));
    }
}
