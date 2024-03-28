package com.fuzzy.subsystems.access;

import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.rdao.database.domainobject.DomainObject;
import com.fuzzy.subsystems.function.BiFunction;
import com.fuzzy.subsystems.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AccessByParentValidator<T extends DomainObject> implements AccessValidator<Long, T> {

    private final ReadableResource<T> readableResource;
    private final AccessValidator<Long, ? extends DomainObject> parentAccessValidator;
    private final Function<T, Long> parentIdGetter;
    private final Map<Long, Boolean> parentWriteAccesses;
    private final Map<Long, Boolean> parentReadAccesses;

    public AccessByParentValidator(@NonNull ResourceProvider resources,
                                   @NonNull Class<T> clazz,
                                   @NonNull AccessValidator<Long, ? extends DomainObject> parentAccessValidator,
                                   @NonNull Function<T, Long> parentIdGetter) {
        this.readableResource = resources.getReadableResource(clazz);
        this.parentAccessValidator = parentAccessValidator;
        this.parentIdGetter = parentIdGetter;
        this.parentWriteAccesses = new HashMap<>();
        this.parentReadAccesses = new HashMap<>();
    }

    @Override
    public boolean checkWriteAccessById(@NonNull Long id,
                                        @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkAccessById(id, context, parentWriteAccesses, parentAccessValidator::checkWriteAccessById);
    }

    @Override
    public boolean checkWriteAccessByObject(@NonNull T object,
                                            @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkAccessByObject(object, context, parentWriteAccesses, parentAccessValidator::checkWriteAccessById);
    }

    @Override
    public boolean checkReadAccessById(@NonNull Long id,
                                       @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkAccessById(id, context, parentReadAccesses, parentAccessValidator::checkReadAccessById);
    }

    @Override
    public boolean checkReadAccessByObject(@NonNull T object,
                                           @NonNull ContextTransactionRequest context) throws PlatformException {
        return checkAccessByObject(object, context, parentReadAccesses, parentAccessValidator::checkReadAccessById);
    }

    private boolean checkAccessByObject(@Nullable T object,
                                        @NonNull ContextTransactionRequest context,
                                        @NonNull Map<Long, Boolean> parentAccesses,
                                        @NonNull BiFunction<Long, ContextTransactionRequest, Boolean> parentAccessGetter) throws PlatformException {
        if (object == null) {
            return false;
        }
        Long parentId = parentIdGetter.apply(object);
        if (parentId == null) {
            return false;
        }
        Boolean access = parentAccesses.get(parentId);
        if (access == null) {
            access = parentAccessGetter.apply(parentId, context);
            parentAccesses.put(parentId, access);
        }
        return access;
    }

    private boolean checkAccessById(@NonNull Long id,
                                    @NonNull ContextTransactionRequest context,
                                    @NonNull Map<Long, Boolean> parentAccesses,
                                    @NonNull BiFunction<Long, ContextTransactionRequest, Boolean> parentAccessGetter) throws PlatformException {
        T object = readableResource.get(id, context.getTransaction());
        return checkAccessByObject(object, context, parentAccesses, parentAccessGetter);
    }
}
