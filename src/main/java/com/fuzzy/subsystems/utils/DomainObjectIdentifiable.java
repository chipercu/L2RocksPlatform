package com.fuzzy.subsystems.utils;

import com.infomaximum.database.domainobject.DomainObject;
import com.fuzzy.subsystems.remote.Identifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

public record DomainObjectIdentifiable<T extends DomainObject>(T object) implements Identifiable<Long> {

    @Override
    public @NonNull Long getIdentifier() {
        return object.getId();
    }
}
