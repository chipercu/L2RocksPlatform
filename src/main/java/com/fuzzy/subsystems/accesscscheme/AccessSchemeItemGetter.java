package com.fuzzy.subsystems.accesscscheme;

import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItemFieldNumberGetter;
import com.fuzzy.subsystems.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccessSchemeItemGetter<S, O, K, T extends DomainObject & AccessSchemeItem<S, O, K>> {

    private final ReadableResource<T> accessSchemeItemReadableResource;
    private final AccessSchemeItemFieldNumberGetter fieldNumberGetter;

    public AccessSchemeItemGetter(@NonNull ResourceProvider resources,
                                  @NonNull Class<T> accessSchemeItemClass,
                                  @NonNull AccessSchemeItemFieldNumberGetter fieldNumberGetter) {
        accessSchemeItemReadableResource = resources.getReadableResource(accessSchemeItemClass);
        this.fieldNumberGetter = fieldNumberGetter;
    }

    public @NonNull Collection<K> getOperations(@NonNull S subjectId,
                                                @NonNull O objectId,
                                                @NonNull QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getSubjectFieldNumber(), getSubjectIdForFilter(subjectId))
                .appendField(fieldNumberGetter.getObjectFieldNumber(), getObjectIdForFilter(objectId));
        List<K> operations = new ArrayList<>();
        accessSchemeItemReadableResource.forEach(filter,
                accessSchemeItem -> operations.add(accessSchemeItem.getOperation()), transaction);
        return operations;
    }

    public boolean checkOperation(@NonNull S subjectId,
                                  @NonNull O objectId,
                                  @NonNull K operation,
                                  @NonNull QueryTransaction transaction) throws PlatformException {
        return getOperations(subjectId, objectId, transaction).contains(operation);
    }

    public void forEachBySubject(@NonNull S subjectId,
                                 @NonNull Function<T, Boolean> handler,
                                 @NonNull QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getSubjectFieldNumber(), getSubjectIdForFilter(subjectId));
        forEachByFilter(filter, handler, transaction);
    }

    public void forEachByObject(@NonNull O objectId,
                                @NonNull Function<T, Boolean> handler,
                                @NonNull QueryTransaction transaction) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getObjectFieldNumber(), getObjectIdForFilter(objectId));
        forEachByFilter(filter, handler, transaction);
    }

    protected @NonNull Object getSubjectIdForFilter(@NonNull S subjectId) {
        return subjectId;
    }

    protected @NonNull Object getObjectIdForFilter(@NonNull O objectId) {
        return objectId;
    }

    private void forEachByFilter(@NonNull HashFilter filter,
                                 @NonNull Function<T, Boolean> handler,
                                 @NonNull QueryTransaction transaction) throws PlatformException {
        try (IteratorEntity<T> ie = accessSchemeItemReadableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                if (!handler.apply(ie.next())) {
                    break;
                }
            }
        }
    }
}
