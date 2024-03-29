package com.fuzzy.subsystem.frontend.domainobject.accessroleprivilege;

import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.frontend.access.FrontendPrivilege;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeEditable;

public class AccessRoleFrontendPrivilegeEditable extends AccessRoleFrontendPrivilegeReadable implements DomainObjectEditable, EntityPrivilegeEditable<FrontendPrivilege> {

    public AccessRoleFrontendPrivilegeEditable(long id) {
        super(id);
    }

    public void setAccessRoleId(Long accessRoleId) {
        set(FIELD_ACCESS_ROLE_ID, accessRoleId);
    }

    @Override
    public void setPrivilege(FrontendPrivilege privilege) {
        set(FIELD_PRIVILEGE, privilege.intValue());
    }

    @Override
    public void setOperations(AccessOperationCollection operations) {
        set(FIELD_OPERATIONS, operations.getValue());
    }
}
