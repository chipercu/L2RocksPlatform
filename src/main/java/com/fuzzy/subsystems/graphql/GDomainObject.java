package com.fuzzy.subsystems.graphql;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.DomainObject;
import com.fuzzy.subsystems.remote.Identifiable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GDomainObject<T extends DomainObject> implements RemoteObject, Identifiable<Long> {

    private final T source;

    public GDomainObject(@NonNull T source) {
        this.source = source;
    }

    public @NonNull T getSource() {
        return source;
    }

    @Override
    public @NonNull Long getIdentifier() {
        return source.getId();
    }
}
