package com.fuzzy.subsystem.core.domainobject.additionalfield;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.anotation.Field;
import com.infomaximum.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystems.remote.RDomainObject;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "AdditionalField",
        fields = {
                @Field(name = "object", number = AdditionalFieldReadable.FIELD_OBJECT_TYPE, type = String.class),
                @Field(name = "key", number = AdditionalFieldReadable.FIELD_KEY, type = String.class),
                @Field(name = "name", number = AdditionalFieldReadable.FIELD_NAME, type = String.class),
                @Field(name = "data_type", number = AdditionalFieldReadable.FIELD_DATA_TYPE, type = Integer.class),
                @Field(name = "index", number = AdditionalFieldReadable.FIELD_INDEX, type = Integer.class),
                @Field(name = "list_source", number = AdditionalFieldReadable.FIELD_LIST_SOURCE, type = String.class),
                @Field(name = "order", number = AdditionalFieldReadable.FIELD_ORDER, type = Integer.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AdditionalFieldReadable.FIELD_OBJECT_TYPE }),
                @HashIndex(fields = { AdditionalFieldReadable.FIELD_OBJECT_TYPE, AdditionalFieldReadable.FIELD_KEY }),
        }
)
public class AdditionalFieldReadable extends RDomainObject {

    public final static int FIELD_OBJECT_TYPE = 0;
    public final static int FIELD_KEY = 1;
    public final static int FIELD_NAME = 2;
    public final static int FIELD_DATA_TYPE = 3;
    public final static int FIELD_INDEX = 4;
    public final static int FIELD_LIST_SOURCE = 5;
    public final static int FIELD_ORDER = 6;

    public AdditionalFieldReadable(long id) {
        super(id);
    }

    public String getObjectType() {
        return get(FIELD_OBJECT_TYPE);
    }

    public String getKey() {
        return get(FIELD_KEY);
    }

    public String getName() {
        return get(FIELD_NAME);
    }

    public FieldDataType getDataType() {
        Integer value = get(FIELD_DATA_TYPE);
        return value != null ? FieldDataType.get(value) : null;
    }

    public int getIndex() {
        return get(FIELD_INDEX);
    }

    public String getListSource() {
        return getString(FIELD_LIST_SOURCE);
    }

    public int getOrder() {
        final Integer value = getInteger(FIELD_ORDER);
        return value != null ? value : 0;
    }
}