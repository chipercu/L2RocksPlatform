package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class StandardAccessSchemeItem<K, S, O> implements IdentifiableAccessSchemeItem<K, S, O, GAccessSchemeOperation> {

    private final AccessSchemeItem<S, O, StandardAccessOperation> item;

    public StandardAccessSchemeItem(@NonNull AccessSchemeItem<S, O, StandardAccessOperation> item) {
        this.item = item;
    }

    @Override
    public @NonNull S getSubjectId() {
        return item.getSubjectId();
    }

    @Override
    public @NonNull O getObjectId() {
        return item.getObjectId();
    }

    @Override
    public @NonNull GAccessSchemeOperation getOperation() {
        return new GAccessSchemeStandardOperation(item.getOperation());
    }
}
