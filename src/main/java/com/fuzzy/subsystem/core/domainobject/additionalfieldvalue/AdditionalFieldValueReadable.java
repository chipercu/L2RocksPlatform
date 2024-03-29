package com.fuzzy.subsystem.core.domainobject.additionalfieldvalue;

import com.fuzzy.main.rdao.database.anotation.Entity;
import com.fuzzy.main.rdao.database.anotation.Field;
import com.fuzzy.main.rdao.database.anotation.HashIndex;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystems.remote.RDomainObject;

import java.time.Instant;
import java.time.LocalDate;

@Entity(
        namespace = CoreSubsystemConsts.UUID,
        name = "AdditionalFieldValue",
        fields = {
                @Field(name = "additional_field_id", number = AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        type = Long.class, foreignDependency = AdditionalFieldReadable.class),
                @Field(name = "object_id", number = AdditionalFieldValueReadable.FIELD_OBJECT_ID,
                        type = Long.class),
                @Field(name = "index", number = AdditionalFieldValueReadable.FIELD_INDEX,
                        type = Integer.class),
                @Field(name = "string_value", number = AdditionalFieldValueReadable.FIELD_STRING_VALUE,
                        type = String.class),
                @Field(name = "long_value", number = AdditionalFieldValueReadable.FIELD_LONG_VALUE,
                        type = Long.class)
        },
        hashIndexes = {
                @HashIndex(fields = { AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID }),
                @HashIndex(fields = {
                        AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        AdditionalFieldValueReadable.FIELD_OBJECT_ID
                }),
                @HashIndex(fields = {
                        AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        AdditionalFieldValueReadable.FIELD_INDEX
                }),
                @HashIndex(fields = {
                        AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        AdditionalFieldValueReadable.FIELD_OBJECT_ID,
                        AdditionalFieldValueReadable.FIELD_INDEX
                }),
                @HashIndex(fields = {
                        AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        AdditionalFieldValueReadable.FIELD_LONG_VALUE,
                        AdditionalFieldValueReadable.FIELD_INDEX
                }),
                @HashIndex(fields = {
                        AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID,
                        AdditionalFieldValueReadable.FIELD_STRING_VALUE,
                        AdditionalFieldValueReadable.FIELD_INDEX
                })
        }
)
public class AdditionalFieldValueReadable extends RDomainObject {

    public final static int FIELD_ADDITIONAL_FIELD_ID = 0;
    public final static int FIELD_OBJECT_ID = 1;
    public final static int FIELD_INDEX = 2;
    public final static int FIELD_STRING_VALUE = 3;
    public final static int FIELD_LONG_VALUE = 4;

    public AdditionalFieldValueReadable(long id) {
        super(id);
    }

    public long getAdditionalFieldId() {
        return get(FIELD_ADDITIONAL_FIELD_ID);
    }

    public long getObjectId() {
        return get(FIELD_OBJECT_ID);
    }

    public int getIndex() {
        return get(FIELD_INDEX);
    }

    public String getStringValue() {
        return get(FIELD_STRING_VALUE);
    }

    public Long getLongValue() {
        return get(FIELD_LONG_VALUE);
    }

    public LocalDate getDateValue() {
        Long value = getLongValue();
        return value != null ? LocalDate.ofEpochDay(value) : null;
    }

    public Instant getDateTimeValue() {
        Long value = getLongValue();
        return value != null ? Instant.ofEpochMilli(value) : null;
    }
}