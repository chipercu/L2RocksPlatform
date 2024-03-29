package com.fuzzy.subsystem.core.remote.fieldsgetter;

import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;

import java.util.Objects;

public class AdditionalFieldDescription extends SystemFieldDescription {

    private final long additionalFieldId;
    private final String name;

    public AdditionalFieldDescription(AdditionalFieldReadable additionalField) {
        super(additionalField.getKey(), additionalField.getDataType());
        this.additionalFieldId = additionalField.getId();
        this.name = additionalField.getName();
    }

    public long getAdditionalFieldId() {
        return additionalFieldId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AdditionalFieldDescription that = (AdditionalFieldDescription) o;
        return additionalFieldId == that.additionalFieldId &&
                Objects.equals(name, that.name);
    }
}
