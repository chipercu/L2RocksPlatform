package com.fuzzy.subsystem.core.remote.fieldsgetter;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SystemFieldDescription implements RemoteObject {

    private final String key;
    private final FieldDataType dataType;

    public SystemFieldDescription(@NonNull String key, @NonNull FieldDataType dataType) {
        this.key = key;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public FieldDataType getDataType() {
        return dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemFieldDescription that = (SystemFieldDescription) o;
        return key.equals(that.key) &&
                dataType == that.dataType;
    }
}
