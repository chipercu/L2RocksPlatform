package com.fuzzy.subsystems.accesscscheme.domainobject;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface AccessSchemeItemEditable<S, O, T> {

    void setSubjectId(@NonNull S subjectId);

    void setObjectId(@NonNull O objectId);

    void setOperation(@NonNull T operation);
}
