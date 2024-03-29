package com.fuzzy.subsystem.core.domainobject.apikey;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.main.rdao.database.anotation.PrefixIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "ApiKey",
        fields = {
                @Field(name = "name", number = ApiKeyReadable.FIELD_NAME, type = String.class),
                @Field(name = "value", number = ApiKeyReadable.FIELD_VALUE, type = String.class),
                @Field(name = "type", number = ApiKeyReadable.FIELD_TYPE, type = String.class),
                @Field(name = "subsystem_uuid", number = ApiKeyReadable.FIELD_SUBSYSTEM_UUID, type = String.class),
                @Field(name = "content", number = ApiKeyReadable.FIELD_CONTENT, type = byte[].class),
        },
        hashIndexes = {
                @HashIndex(fields = { ApiKeyReadable.FIELD_NAME}),
                @HashIndex(fields = { ApiKeyReadable.FIELD_VALUE}),
                @HashIndex(fields = { ApiKeyReadable.FIELD_TYPE}),
        },
        prefixIndexes = {
                @PrefixIndex(fields = { ApiKeyReadable.FIELD_NAME })
        }
)
public class ApiKeyReadable extends RDomainObject {

    public final static int FIELD_NAME = 0;
    public final static int FIELD_VALUE = 1;
    public final static int FIELD_TYPE = 2;
    public final static int FIELD_SUBSYSTEM_UUID = 3;
    public final static int FIELD_CONTENT = 4;

    public ApiKeyReadable(long id) {
        super(id);
    }

    public String getName() {
        return getString(FIELD_NAME);
    }

    public String getValue() {
        return getString(FIELD_VALUE);
    }

    public String getType() {
        return getString(FIELD_TYPE);
    }

    public String getSubsystemUuid() {
        return getString(FIELD_SUBSYSTEM_UUID);
    }

    public byte[] getContent() {
        return get(FIELD_CONTENT);
    }
}
