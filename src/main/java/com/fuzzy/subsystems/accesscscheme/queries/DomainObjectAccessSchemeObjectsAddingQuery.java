package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

public class DomainObjectAccessSchemeObjectsAddingQuery<
        T,
        U extends AccessSchemeItem<Long, Long, T>> extends AccessSchemeObjectsAddingQuery<Long, Long, T, U> {

    public DomainObjectAccessSchemeObjectsAddingQuery(@NonNull AccessSchemeProcessor<Long, Long, T, U> accessSchemeProcessor,
                                                      @NonNull Class<? extends DomainObject> subjectClass,
                                                      @NonNull Class<? extends DomainObject> objectClass,
                                                      @NonNull Long subjectId,
                                                      @NonNull HashSet<Long> objectIds,
                                                      @NonNull T operation) {
        super(accessSchemeProcessor,
                new DomainObjectSubjectObjectChecker<>(subjectClass, objectClass),
                subjectId,
                objectIds,
                operation);
    }
}
