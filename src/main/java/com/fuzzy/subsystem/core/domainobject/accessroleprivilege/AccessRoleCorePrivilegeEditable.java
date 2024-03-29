package com.fuzzy.subsystem.core.domainobject.accessroleprivilege;

import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeEditable;

public class AccessRoleCorePrivilegeEditable extends AccessRoleCorePrivilegeReadable
        implements DomainObjectEditable, EntityPrivilegeEditable<CorePrivilege> {

    public AccessRoleCorePrivilegeEditable(long id) {
        super(id);
    }

    public void setAccessRoleId(Long accessRoleId) {
        set(FIELD_ACCESS_ROLE_ID, accessRoleId);
    }

    public void setPrivilege(CorePrivilege privilege) {
        set(FIELD_PRIVILEGE, privilege.intValue());
    }

    public void setOperations(AccessOperationCollection operations) {
        set(FIELD_OPERATIONS, operations.getValue());
    }
}