package com.fuzzy.subsystems.utils;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.iterator.IteratorEntity;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Supplier;

public class DomainObjectValidator {

    public static void validateUnique(int fieldNumber,
                                      @Nullable Object fieldValue,
                                      @Nullable Long excludedId,
                                      @NonNull ReadableResource<? extends DomainObject> readableResource,
                                      @NonNull QueryTransaction transaction) throws PlatformException {
        validateUnique(fieldNumber, fieldValue, excludedId, readableResource, transaction,
                () -> GeneralExceptionBuilder.buildNotUniqueValueException(
                        readableResource.getDomainClass(), fieldNumber, fieldValue));
    }

    public static void validateUnique(int fieldNumber,
                                      @Nullable Object fieldValue,
                                      @Nullable Long excludedId,
                                      @NonNull ReadableResource<? extends DomainObject> readableResource,
                                      @NonNull QueryTransaction transaction,
                                      @NonNull Supplier<PlatformException> exceptionSupplier) throws PlatformException {
        HashFilter filter = new HashFilter(fieldNumber, fieldValue);
        validateUnique(filter, excludedId, readableResource, transaction, exceptionSupplier);
    }

    public static void validateUnique(@NonNull HashFilter filter,
                                      @Nullable Long excludedId,
                                      @NonNull ReadableResource<? extends DomainObject> readableResource,
                                      @NonNull QueryTransaction transaction,
                                      @NonNull Supplier<PlatformException> exceptionSupplier) throws PlatformException {
        try (IteratorEntity<? extends DomainObject> ie = readableResource.findAll(filter, transaction)) {
            while (ie.hasNext()) {
                DomainObject domainObject = ie.next();
                if (excludedId == null || !excludedId.equals(domainObject.getId())) {
                    throw exceptionSupplier.get();
                }
            }
        }
    }

    public static void validateNonEmpty(int fieldNumber,
                                        @Nullable String fieldValue,
                                        @NonNull Class<? extends DomainObject> clazz) throws PlatformException {
        if (StringUtils.isEmpty(fieldValue)) {
            throw GeneralExceptionBuilder.buildEmptyValueException(clazz, fieldNumber);
        }
    }

    public static void validateNonEmptyAndUnique(int fieldNumber,
                                                 @Nullable String fieldValue,
                                                 @Nullable Long excludedId,
                                                 @NonNull ReadableResource<? extends DomainObject> readableResource,
                                                 @NonNull QueryTransaction transaction) throws PlatformException {
        validateNonEmpty(fieldNumber, fieldValue, readableResource.getDomainClass());
        validateUnique(fieldNumber, fieldValue, excludedId, readableResource, transaction);
    }
}
