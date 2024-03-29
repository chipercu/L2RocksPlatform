package com.fuzzy.subsystem.core.domainobject.apikeyprivilege;

import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeEditable;

public class ApiKeyCorePrivilegeEditable extends ApiKeyCorePrivilegeReadable
        implements DomainObjectEditable, EntityPrivilegeEditable<CorePrivilege> {

    public ApiKeyCorePrivilegeEditable(long id) {
        super(id);
    }

    public void setApiKeyId(Long apiKeyId) {
        set(FIELD_API_KEY_ID, apiKeyId);
    }

    public void setPrivilege(CorePrivilege privilege) {
        set(FIELD_PRIVILEGE, privilege.intValue());
    }

    public void setOperations(AccessOperationCollection operations) {
        set(FIELD_OPERATIONS, operations.getValue());
    }
}
