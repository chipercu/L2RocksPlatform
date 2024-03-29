package com.fuzzy.subsystem.core.domainobject.additionalfieldvalue;

import com.infomaximum.database.domainobject.DomainObjectEditable;

import java.time.Instant;
import java.time.LocalDate;

public class AdditionalFieldValueEditable extends AdditionalFieldValueReadable implements DomainObjectEditable {

    public AdditionalFieldValueEditable(long id) {
        super(id);
    }

    public void setAdditionalFieldId(long value) {
        set(FIELD_ADDITIONAL_FIELD_ID, value);
    }

    public void setObjectId(long value) {
        set(FIELD_OBJECT_ID, value);
    }

    public void setIndex(int value) {
        set(FIELD_INDEX, value);
    }

    public void setStringValue(String value) {
        set(FIELD_STRING_VALUE, value);
    }

    public void setLongValue(Long value) {
        set(FIELD_LONG_VALUE, value);
    }

    public void setDateValue(LocalDate value) {
        setLongValue(value != null ? value.toEpochDay() : null);
    }

    public void setDateTimeValue(Instant value) {
        setLongValue(value != null ? value.toEpochMilli() : null);
    }
}