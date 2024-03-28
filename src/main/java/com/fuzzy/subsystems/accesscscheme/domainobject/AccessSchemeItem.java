package com.fuzzy.subsystems.accesscscheme.domainobject;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface AccessSchemeItem<S, O, T> {

    @NonNull S getSubjectId();

    @NonNull O getObjectId();

    @NonNull T getOperation();
}
