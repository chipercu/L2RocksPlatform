package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItemEditable;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItemFieldNumberGetter;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DomainObjectAccessSchemeItemSetter<
        T,
        U extends DomainObject,
        K extends DomainObject,
        V extends DomainObject & DomainObjectEditable & AccessSchemeItem<Long, Long, T> & AccessSchemeItemEditable<Long, Long, T>>
        extends AccessSchemeItemSetter<Long, Long, T, U, K, V> {

    private final ReadableResource<U> subjectReadableResource;
    private final ReadableResource<K> objectReadableResource;

    public DomainObjectAccessSchemeItemSetter(@NonNull ResourceProvider resources,
                                              @NonNull Class<U> subjectClass,
                                              @NonNull Class<K> objectClass,
                                              @NonNull Class<V> accessSchemeItemClass,
                                              @NonNull AccessSchemeItemFieldNumberGetter fieldNumberGetter) {
        super(resources, accessSchemeItemClass, fieldNumberGetter);
        subjectReadableResource = resources.getReadableResource(subjectClass);
        objectReadableResource = resources.getReadableResource(objectClass);

    }

    @Override
    protected @NonNull U validateAndGetSubject(@NonNull Long subjectId,
                                               @NonNull QueryTransaction transaction) throws PlatformException {
        PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(false);
        return primaryKeyValidator.validateAndGet(subjectId, subjectReadableResource, transaction);
    }

    @Override
    protected @NonNull K validateAndGetObject(@NonNull Long objectId,
                                              @NonNull QueryTransaction transaction) throws PlatformException {
        PrimaryKeyValidator primaryKeyValidator = new PrimaryKeyValidator(false);
        return primaryKeyValidator.validateAndGet(objectId, objectReadableResource, transaction);
    }
}
