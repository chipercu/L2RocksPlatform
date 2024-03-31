package com.fuzzy.subsystem.core.remote.additionalfield;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.cluster.graphql.struct.GOptional;
import com.fuzzy.subsystem.core.enums.FieldDataType;

public class AdditionalFieldUpdatingBuilder implements RemoteObject {

    private transient GOptional<String> name = GOptional.notPresent();
    private transient GOptional<FieldDataType> dataType = GOptional.notPresent();

    public void setName(String name) {
        this.name = GOptional.of(name);
    }

    public void setDataType(FieldDataType dataType) {
        this.dataType = GOptional.of(dataType);
    }

    public GOptional<String> getName() {
        return name;
    }

    public GOptional<FieldDataType> getDataType() {
        return dataType;
    }

}
