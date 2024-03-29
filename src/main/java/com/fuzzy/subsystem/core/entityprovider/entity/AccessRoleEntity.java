package com.fuzzy.subsystem.core.entityprovider.entity;

import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.accessrole.AccessRoleReadable;
import com.fuzzy.subsystem.core.entityprovider.datasources.AccessRoleDataSource;
import com.infomaximum.subsystem.entityprovidersdk.entity.DataContainer;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityClass;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityField;
import com.infomaximum.subsystem.entityprovidersdk.entity.Id;
import com.infomaximum.subsystem.entityprovidersdk.enums.DataType;


@EntityClass(
        name = "access_role",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = AccessRoleDataSource.class)
public class AccessRoleEntity implements DataContainer {
    private final AccessRoleReadable accessRoleReadable;

    public AccessRoleEntity(AccessRoleReadable accessRoleReadable) {
        this.accessRoleReadable = accessRoleReadable;
    }

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return accessRoleReadable.getId();
    }

    @EntityField(name = "name", type = DataType.STRING)
    public String getName() {
        return accessRoleReadable.getName();
    }

    @EntityField(name = "is_admin", type = DataType.BOOLEAN)
    public Boolean getAdmin() {
        return accessRoleReadable.isAdmin();
    }
}
