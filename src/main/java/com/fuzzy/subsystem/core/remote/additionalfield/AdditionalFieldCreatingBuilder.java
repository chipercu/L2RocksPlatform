package com.fuzzy.subsystem.core.remote.additionalfield;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.enums.ListSourceType;

public class AdditionalFieldCreatingBuilder implements RemoteObject {

    private final String objectType;
    private final String name;
    private final FieldDataType dataType;
    private final ListSourceType listSource;

    public AdditionalFieldCreatingBuilder(String objectType, String name, FieldDataType dataType) {
        this(objectType, name, dataType, null);
    }

    public AdditionalFieldCreatingBuilder(String objectType, String name, FieldDataType dataType, ListSourceType listSource) {
        this.objectType = objectType;
        this.name = name;
        this.dataType = dataType;
        this.listSource = listSource;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getName() {
        return name;
    }

    public FieldDataType getDataType() {
        return dataType;
    }

    public ListSourceType getListSource() {
        return listSource;
    }
}
