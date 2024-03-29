package com.fuzzy.subsystem.core.domainobject.apikey;

import com.infomaximum.database.domainobject.DomainObjectEditable;

public class ApiKeyEditable extends ApiKeyReadable implements DomainObjectEditable {

    public ApiKeyEditable(long id) {
        super(id);
    }

    public void setName(String name) {
        set(FIELD_NAME, name);
    }

    public void setValue(String value) {
        set(FIELD_VALUE, value);
    }

    public void setType(String type) {
        set(FIELD_TYPE, type);
    }

    public void setType(ApiKeyType type) {
        set(FIELD_TYPE, type.getType());
        set(FIELD_SUBSYSTEM_UUID, type.getSubsystemUuid());
    }

    public void setSubsystemUuid(String uuid) {
        set(FIELD_SUBSYSTEM_UUID, uuid);
    }

    public void setContent(byte[] content) {
        set(FIELD_CONTENT, content);
    }
}
