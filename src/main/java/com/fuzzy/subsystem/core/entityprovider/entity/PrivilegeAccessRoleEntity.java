package com.fuzzy.subsystem.core.entityprovider.entity;


import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.entityprovider.datasources.PrivilegeAccessRoleDataSource;
import com.infomaximum.subsystem.entityprovidersdk.entity.DataContainer;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityClass;
import com.infomaximum.subsystem.entityprovidersdk.entity.EntityField;
import com.infomaximum.subsystem.entityprovidersdk.entity.Id;
import com.infomaximum.subsystem.entityprovidersdk.enums.DataType;

import java.util.Objects;


@EntityClass(
        name = "access_role_privilege",
        uuid = CoreSubsystemConsts.UUID,
        dataSource = PrivilegeAccessRoleDataSource.class)

public class PrivilegeAccessRoleEntity implements DataContainer {

    private final Long accessRoleId;
    private final String name;
    private final String nameRu;
    private final Boolean read;
    private final Boolean write;
    private final Boolean create;
    private final Boolean delete;
    private final Boolean execute;
    private final long id;

    @Id
    @EntityField(name = "id", type = DataType.LONG)
    public long getId() {
        return id;
    }

    @EntityField(name = "access_role_id", type = DataType.LONG)
    public Long getAccessRoleId() {
        return accessRoleId;
    }

    @EntityField(name = "name", type = DataType.STRING)
    public String getName() {
        return name;
    }

    @EntityField(name = "name_ru", type = DataType.STRING)
    public String getNameRu() {
        return nameRu;
    }

    @EntityField(name = "is_read", type = DataType.BOOLEAN)
    public Boolean getRead() {
        return read;
    }

    @EntityField(name = "is_write", type = DataType.BOOLEAN)
    public Boolean getWrite() {
        return write;
    }

    @EntityField(name = "is_create", type = DataType.BOOLEAN)
    public Boolean getCreate() {
        return create;
    }

    @EntityField(name = "is_delete", type = DataType.BOOLEAN)
    public Boolean getDelete() {
        return delete;
    }

    @EntityField(name = "is_execute", type = DataType.BOOLEAN)
    public Boolean getExecute() {
        return execute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessRoleId, name, read, write, create, delete, execute, nameRu);
    }

    private PrivilegeAccessRoleEntity(Builder builder) {
        accessRoleId = builder.accessRoleId;
        name = builder.name;
        nameRu = builder.nameRu;
        read = builder.read;
        write = builder.write;
        create = builder.create;
        delete = builder.delete;
        execute = builder.execute;
        id = builder.id;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long id;
        private Long accessRoleId;
        private String name;
        private String nameRu;
        private Boolean read;
        private Boolean write;
        private Boolean create;
        private Boolean delete;
        private Boolean execute;

        private Builder() {
        }

        public Builder withAccessRoleId(Long val) {
            accessRoleId = val;
            return this;
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withNameRu(String val) {
            nameRu = val;
            return this;
        }

        public Builder withRead(Boolean val) {
            read = val;
            return this;
        }

        public Builder withWrite(Boolean val) {
            write = val;
            return this;
        }

        public Builder withCreate(Boolean val) {
            create = val;
            return this;
        }

        public Builder withDelete(Boolean val) {
            delete = val;
            return this;
        }

        public Builder withExecute(Boolean val) {
            execute = val;
            return this;
        }

        public Builder withId(long val) {
            id = val;
            return this;
        }

        public PrivilegeAccessRoleEntity build() {
            return new PrivilegeAccessRoleEntity(this);
        }
    }
}