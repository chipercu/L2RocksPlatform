package com.fuzzy.subsystems.accesscscheme.queries;

import com.infomaximum.database.domainobject.DomainObject;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class DomainObjectAccessSchemeRemovingQuery<
        T,
        S extends DomainObject,
        O extends DomainObject,
        U extends DomainObject & AccessSchemeItem<Long, Long, T>> extends AccessSchemeRemovingQuery<Long, Long, T, U> {

    public DomainObjectAccessSchemeRemovingQuery(@NonNull Class<S> subjectClass,
                                                 @NonNull Class<O> objectClass,
                                                 @NonNull AccessSchemeProcessor<Long, Long, T, U> accessSchemeProcessor,
                                                 @NonNull ArrayList<? extends AccessSchemeItem<Long, Long, T>> accessItems) {
        super(accessSchemeProcessor, new DomainObjectSubjectObjectChecker<>(subjectClass, objectClass), accessItems);
    }
}
