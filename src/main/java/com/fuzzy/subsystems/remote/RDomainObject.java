package com.fuzzy.subsystems.remote;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Function;

public class RDomainObject extends DomainObject implements Identifiable<Long> {

    public RDomainObject(long id) {
        super(id);
    }

    @Override
    public @NonNull Long getIdentifier() {
        return getId();
    }

    protected <T> @NonNull T getEnumValue(int fieldNumber,
                                          @NonNull T defaultValue,
                                          @NonNull Function<Integer, T> converter) {
        Integer value = get(fieldNumber);
        T result = value != null ? converter.apply(value) : null;
        return result != null ? result : defaultValue;
    }
}
