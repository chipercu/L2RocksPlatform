package com.fuzzy.subsystem.core.domainobject.additionalfield;

import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.fuzzy.subsystem.core.enums.FieldDataType;

public class AdditionalFieldEditable extends AdditionalFieldReadable implements DomainObjectEditable {

    public AdditionalFieldEditable(long id) {
        super(id);
    }

    public void setObjectType(String value) {
        set(FIELD_OBJECT_TYPE, value);
    }

    public void setKey(String value) {
        set(FIELD_KEY, value);
    }

    public void setName(String value) {
        set(FIELD_NAME, value);
    }

    public void setDataType(FieldDataType value) {
        set(FIELD_DATA_TYPE, value != null ? value.intValue() : null);
    }

    public void setIndex(int value) {
        set(FIELD_INDEX, value);
    }

    public void setListSource(String value) {
        set(FIELD_LIST_SOURCE, value);
    }

    public void setOrder(int value) {
        set(FIELD_ORDER, value);
    }

}