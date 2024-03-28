package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashSet;

public class DomainObjectAccessSchemeSubjectsAddingQuery<
        T,
        U extends AccessSchemeItem<Long, Long, T>> extends AccessSchemeSubjectsAddingQuery<Long, Long, T, U> {

    public DomainObjectAccessSchemeSubjectsAddingQuery(@NonNull AccessSchemeProcessor<Long, Long, T, U> accessSchemeProcessor,
                                                       @NonNull Class<? extends DomainObject> subjectClass,
                                                       @NonNull Class<? extends DomainObject> objectClass,
                                                       @NonNull Long objectId,
                                                       @NonNull HashSet<Long> subjectIds,
                                                       @NonNull T operation) {
        super(accessSchemeProcessor,
                new DomainObjectSubjectObjectChecker<>(subjectClass, objectClass),
                objectId,
                subjectIds,
                operation);
    }
}