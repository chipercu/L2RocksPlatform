package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import org.checkerframework.checker.nullness.qual.NonNull;

class DomainObjectSubjectObjectChecker<
        T extends DomainObject,
        Y extends DomainObject> implements SubjectObjectChecker<Long, Long> {

    private final Class<T> subjectClass;
    private final Class<Y> objectClass;

    private ReadableResource<T> subjectReadableResource;
    private ReadableResource<Y> objectReadableResource;

    public DomainObjectSubjectObjectChecker(@NonNull Class<T> subjectClass,
                                            @NonNull Class<Y> objectClass) {
        this.subjectClass = subjectClass;
        this.objectClass = objectClass;
    }

    @Override
    public void prepare(@NonNull ResourceProvider resources) {
        subjectReadableResource = resources.getReadableResource(subjectClass);
        objectReadableResource = resources.getReadableResource(objectClass);
    }

    @Override
    public boolean checkSubject(@NonNull Long subjectId, @NonNull QueryTransaction transaction) throws PlatformException {
        return subjectReadableResource.get(subjectId, transaction) != null;
    }

    @Override
    public boolean checkObject(@NonNull Long objectId, @NonNull QueryTransaction transaction) throws PlatformException {
        return objectReadableResource.get(objectId, transaction) != null;
    }
}
