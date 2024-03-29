package com.fuzzy.subsystems.accesscscheme;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItem;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItemEditable;
import com.fuzzy.subsystems.accesscscheme.domainobject.AccessSchemeItemFieldNumberGetter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Predicate;

public abstract class AccessSchemeItemSetter<
        S, O, T, U, K,
        V extends DomainObject & DomainObjectEditable & AccessSchemeItem<S, O, T> & AccessSchemeItemEditable<S, O, T>> {

    private final RemovableResource<V> accessSchemeItemRemovableResource;
    private final AccessSchemeItemFieldNumberGetter fieldNumberGetter;

    public AccessSchemeItemSetter(@NonNull ResourceProvider resources,
                                  @NonNull Class<V> accessSchemeItemClass,
                                  @NonNull AccessSchemeItemFieldNumberGetter fieldNumberGetter) {
        accessSchemeItemRemovableResource = resources.getRemovableResource(accessSchemeItemClass);
        this.fieldNumberGetter = fieldNumberGetter;
    }

    public void add(@NonNull S subjectId,
                    @NonNull O objectId,
                    @NonNull T operation,
                    @NonNull ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        U subject = validateAndGetSubject(subjectId, transaction);
        K object = validateAndGetObject(objectId, transaction);
        HashFilter filter = new HashFilter(fieldNumberGetter.getSubjectFieldNumber(), getSubjectIdForFilter(subjectId))
                .appendField(fieldNumberGetter.getObjectFieldNumber(), getObjectIdForFilter(objectId));
        boolean exists = false;
        try (IteratorEntity<V> ie = accessSchemeItemRemovableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                if (ie.next().getOperation() == operation) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            V accessSchemeItem = accessSchemeItemRemovableResource.create(transaction);
            accessSchemeItem.setSubjectId(subjectId);
            accessSchemeItem.setObjectId(objectId);
            accessSchemeItem.setOperation(operation);
            accessSchemeItemRemovableResource.save(accessSchemeItem, transaction);
            onChange(subject, object, accessSchemeItem.getOperation(), Action.INSERT, context);
        }
    }

    public void remove(@NonNull S subjectId,
                       @NonNull O objectId,
                       @NonNull T operation,
                       @NonNull ContextTransaction<?> context) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getSubjectFieldNumber(), getSubjectIdForFilter(subjectId))
                .appendField(fieldNumberGetter.getObjectFieldNumber(), getObjectIdForFilter(objectId));
        remove(subjectId, objectId, filter, item -> item.getOperation() == operation, context);
    }

    public void removeSubject(@NonNull S subjectId, @NonNull ContextTransaction<?> context) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getSubjectFieldNumber(), getSubjectIdForFilter(subjectId));
        remove(subjectId, null, filter, item -> true, context);
    }

    public void removeObject(@NonNull O objectId, @NonNull ContextTransaction<?> context) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumberGetter.getObjectFieldNumber(), getObjectIdForFilter(objectId));
        remove(null, objectId, filter, item -> true,context);
    }

    protected abstract @NonNull U validateAndGetSubject(@NonNull S subjectId,
                                                        @NonNull QueryTransaction transaction) throws PlatformException;

    protected abstract @NonNull K validateAndGetObject(@NonNull O objectId,
                                                       @NonNull QueryTransaction transaction) throws PlatformException;

    protected void onChange(@NonNull U subject,
                            @NonNull K object,
                            @NonNull T operation,
                            @NonNull Action action,
                            @NonNull ContextTransaction<?> context) throws PlatformException { }

    protected @NonNull Object getSubjectIdForFilter(@NonNull S subjectId) {
        return subjectId;
    }

    protected @NonNull Object getObjectIdForFilter(@NonNull O objectId) {
        return objectId;
    }

    public enum Action {
        INSERT, REMOVE
    }

    private void remove(@Nullable S subjectId,
                        @Nullable O objectId,
                        @NonNull HashFilter filter,
                        @NonNull Predicate<V> predicate,
                        @NonNull ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        U subject = null;
        K object = null;
        try (IteratorEntity<V> ie = accessSchemeItemRemovableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                V accessSchemeItem = ie.next();
                if (predicate.test(accessSchemeItem)) {
                    accessSchemeItemRemovableResource.remove(accessSchemeItem, transaction);
                    if (subjectId == null || subject == null) {
                        subject = validateAndGetSubject(accessSchemeItem.getSubjectId(), transaction);
                    }
                    if (objectId == null || object == null) {
                        object = validateAndGetObject(accessSchemeItem.getObjectId(), transaction);
                    }
                    onChange(subject, object, accessSchemeItem.getOperation(), Action.REMOVE, context);
                }
            }
        }
    }
}
