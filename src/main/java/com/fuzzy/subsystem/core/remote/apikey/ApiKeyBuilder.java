package com.fuzzy.subsystem.core.remote.apikey;

import com.infomaximum.platform.exception.PlatformException;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyReadable;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyType;
import com.fuzzy.subsystem.core.domainobject.apikey.ApiKeyTypes;
import com.fuzzy.subsystems.modelspace.BuilderFields;

public class ApiKeyBuilder extends BuilderFields {

    public ApiKeyBuilder withName(String name) {
        fields.put(ApiKeyReadable.FIELD_NAME, name);
        return this;
    }
    public ApiKeyBuilder withValue(String value) {
        fields.put(ApiKeyReadable.FIELD_VALUE, value);
        return this;
    }
    public ApiKeyBuilder withType(ApiKeyType type) {
        fields.put(ApiKeyReadable.FIELD_TYPE, type.getType());
        fields.put(ApiKeyReadable.FIELD_SUBSYSTEM_UUID, type.getSubsystemUuid());
        return this;
    }

    public ApiKeyBuilder withContent(byte[] content) {
        with(ApiKeyReadable.FIELD_CONTENT, content);
        return this;
    }

    public boolean isContainsName() {
        return fields.containsKey(ApiKeyReadable.FIELD_NAME);
    }
    public boolean isContainsValue() {
        return fields.containsKey(ApiKeyReadable.FIELD_VALUE);
    }
    public boolean isContainsType() {
        return fields.containsKey(ApiKeyReadable.FIELD_TYPE);
    }
    public boolean isContainsContent() {
        return fields.containsKey(ApiKeyReadable.FIELD_CONTENT);
    }

    public String getName() {
        return (String) fields.get(ApiKeyReadable.FIELD_NAME);
    }
    public ApiKeyType getType() throws PlatformException {
		String type = (String) fields.get(ApiKeyReadable.FIELD_TYPE);
		String subsystemUuid = (String) fields.get(ApiKeyReadable.FIELD_SUBSYSTEM_UUID);
        return ApiKeyTypes.instanceOf(type, subsystemUuid);
    }
    public String getValue() {
        return (String) fields.get(ApiKeyReadable.FIELD_VALUE);
    }
    public byte[] getContent() {
        return (byte[]) fields.get(ApiKeyReadable.FIELD_CONTENT);
    }
}
