package com.fuzzy.subsystem.core.domainobject.apikeyprivilege;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.access.CorePrivilege;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystems.access.AccessOperationCollection;
import com.fuzzy.subsystems.entityprivilege.EntityPrivilegeReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "ApiKeyCorePrivilege",
        fields = {
                @Field(name = "api_key_id", number = ApiKeyCorePrivilegeReadable.FIELD_API_KEY_ID,
                        type = Long.class, foreignDependency = ApiKeyReadable.class),
                @Field(name = "privilege", number = ApiKeyCorePrivilegeReadable.FIELD_PRIVILEGE,
                        type = Integer.class),
                @Field(name = "operations", number = ApiKeyCorePrivilegeReadable.FIELD_OPERATIONS,
                        type = Integer.class)
        },
        hashIndexes = {
                @HashIndex(fields = { ApiKeyCorePrivilegeReadable.FIELD_API_KEY_ID})
        }
)
public class ApiKeyCorePrivilegeReadable extends RDomainObject implements EntityPrivilegeReadable<CorePrivilege> {

    public final static int FIELD_API_KEY_ID = EntityPrivilegeReadable.FIELD_OPERATIONS + 1;

    public ApiKeyCorePrivilegeReadable(long id) {
        super(id);
    }

    public Long getApiKeyId() {
        return getLong(FIELD_API_KEY_ID);
    }

    public CorePrivilege getPrivilege() {
        return CorePrivilege.valueOf(getInteger(FIELD_PRIVILEGE));
    }

    public AccessOperationCollection getOperations() {
        return new AccessOperationCollection(getInteger(FIELD_OPERATIONS));
    }
}
