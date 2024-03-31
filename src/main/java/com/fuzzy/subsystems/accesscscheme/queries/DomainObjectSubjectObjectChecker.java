package com.fuzzy.subsystems.accesscscheme.queries;

import com.fuzzy.database.domainobject.DomainObject;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DomainObjectSubjectObjectChecker<
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
