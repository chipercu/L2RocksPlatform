package com.fuzzy.subsystem.core.remote.additionalfieldvaluegetter;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.AbstractQueryRController;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystems.remote.Optional;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RCAdditionalFieldValueGetterImpl extends AbstractQueryRController<CoreSubsystem>
        implements RCAdditionalFieldValueGetter {

    private final ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private final ReadableResource<AdditionalFieldValueReadable> additionalFieldValueReadableResource;

    public RCAdditionalFieldValueGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        additionalFieldValueReadableResource = resources.getReadableResource(AdditionalFieldValueReadable.class);
    }

    @Override
    public Optional<Long> getLongValue(long additionalFieldId,
                                       long objectId,
                                       int index,
                                       @NonNull ContextTransaction context) throws PlatformException {
        AdditionalFieldValueReadable value = getValue(additionalFieldId, objectId, index,
                EnumSet.of(FieldDataType.LONG, FieldDataType.LONG_ARRAY), context);
        return value != null ? Optional.of(value.getLongValue()) : Optional.notPresent();
    }

    @Override
    public Optional<Long> getIdValue(long additionalFieldId,
                                     long objectId,
                                     int index,
                                     @NonNull ContextTransaction context) throws PlatformException {
        AdditionalFieldValueReadable value = getValue(additionalFieldId, objectId, index,
                EnumSet.of(FieldDataType.ID, FieldDataType.LONG_ARRAY), context);
        return value != null ? Optional.of(value.getLongValue()) : Optional.notPresent();
    }

    @Override
    public HashSet<Long> findObjects(long additionalFieldId,
                                     Long value,
                                     @NonNull ContextTransaction context) throws PlatformException {
        return findObjects(additionalFieldId, value, FieldDataType.LONG, context);
    }

    @Override
    public @NonNull ArrayList<Long> getLongArray(long additionalFieldId,
                                                 long objectId,
                                                 @NonNull ContextTransaction context) throws PlatformException {
        return getArray(additionalFieldId, objectId, FieldDataType.LONG_ARRAY,
                AdditionalFieldValueReadable::getLongValue, context);
    }

    @Override
    public Optional<String> getStringValue(long additionalFieldId,
                                           long objectId,
                                           int index,
                                           @NonNull ContextTransaction context) throws PlatformException {
        AdditionalFieldValueReadable value = getValue(additionalFieldId, objectId, index,
                EnumSet.of(FieldDataType.STRING, FieldDataType.STRING_ARRAY), context);
        return value != null ? Optional.of(value.getStringValue()) : Optional.notPresent();
    }

    @Override
    public HashSet<Long> findObjects(long additionalFieldId,
                                     String value,
                                     @NonNull ContextTransaction context) throws PlatformException {
        return findObjects(additionalFieldId, value, FieldDataType.STRING, context);
    }

    @Override
    public @NonNull ArrayList<String> getStringArray(long additionalFieldId,
                                                     long objectId,
                                                     @NonNull ContextTransaction context) throws PlatformException {
        return getArray(additionalFieldId, objectId, FieldDataType.STRING_ARRAY,
                AdditionalFieldValueReadable::getStringValue, context);
    }

    @Override
    public Optional<LocalDate> getDateValue(long additionalFieldId,
                                            long objectId,
                                            int index,
                                            @NonNull ContextTransaction context) throws PlatformException {
        AdditionalFieldValueReadable value = getValue(additionalFieldId, objectId, index,
                EnumSet.of(FieldDataType.DATE, FieldDataType.DATE_ARRAY), context);
        return value != null ? Optional.of(value.getDateValue()) : Optional.notPresent();
    }

    @Override
    public HashSet<Long> findObjects(long additionalFieldId,
                                     LocalDate value,
                                     @NonNull ContextTransaction context) throws PlatformException {
        return findObjects(additionalFieldId, value, FieldDataType.DATE, context);
    }

    @Override
    public @NonNull ArrayList<LocalDate> getDateArray(long additionalFieldId,
                                                      long objectId,
                                                      @NonNull ContextTransaction context) throws PlatformException {
        return getArray(additionalFieldId, objectId, FieldDataType.DATE_ARRAY,
                AdditionalFieldValueReadable::getDateValue, context);
    }

    @Override
    public Optional<Instant> getDateTimeValue(long additionalFieldId,
                                              long objectId,
                                              int index,
                                              @NonNull ContextTransaction context) throws PlatformException {
        AdditionalFieldValueReadable value = getValue(additionalFieldId, objectId, index,
                EnumSet.of(FieldDataType.DATETIME, FieldDataType.DATETIME_ARRAY), context);
        return value != null ? Optional.of(value.getDateTimeValue()) : Optional.notPresent();
    }

    @Override
    public HashSet<Long> findObjects(long additionalFieldId,
                                     Instant value,
                                     @NonNull ContextTransaction context) throws PlatformException {
        return findObjects(additionalFieldId, value, FieldDataType.DATETIME, context);
    }

    @Override
    public @NonNull ArrayList<Instant> getDateTimeArray(long additionalFieldId,
                                                        long objectId,
                                                        @NonNull ContextTransaction context) throws PlatformException {
        return getArray(additionalFieldId, objectId, FieldDataType.DATETIME_ARRAY,
                AdditionalFieldValueReadable::getDateTimeValue, context);
    }

    private AdditionalFieldValueReadable getValue(long additionalFieldId,
                                                  long objectId,
                                                  int index,
                                                  EnumSet<FieldDataType> dataTypes,
                                                  ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        validateAdditionalField(additionalFieldId, dataTypes, transaction);
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId)
                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, index);
        return additionalFieldValueReadableResource.find(filter, transaction);
    }

    private HashSet<Long> findObjects(long additionalFieldId,
                                      Object value,
                                      FieldDataType dataType,
                                      ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        validateAdditionalField(additionalFieldId, EnumSet.of(dataType), transaction);
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, 0);
        switch (dataType) {
            case STRING:
                filter.appendField(AdditionalFieldValueReadable.FIELD_STRING_VALUE, value);
                break;
            case LONG:
                filter.appendField(AdditionalFieldValueReadable.FIELD_LONG_VALUE, value);
                break;
            case DATE:
                filter.appendField(AdditionalFieldValueReadable.FIELD_LONG_VALUE,
                        value != null ? ((LocalDate) value).toEpochDay() : null);
                break;
            case DATETIME:
                filter.appendField(AdditionalFieldValueReadable.FIELD_LONG_VALUE,
                        value != null ? ((Instant) value).toEpochMilli() : null);
                break;
            case STRING_ARRAY:
            case LONG_ARRAY:
            case DATE_ARRAY:
            case DATETIME_ARRAY:
                throw GeneralExceptionBuilder.buildUnexpectedBehaviourException("Некорректный dataType");
        }
        HashSet<Long> objects = new HashSet<>();
        additionalFieldValueReadableResource.forEach(filter, additionalFieldValue ->
                objects.add(additionalFieldValue.getObjectId()), transaction);
        return objects;
    }

    private <T> ArrayList<T> getArray(long additionalFieldId,
                                      long objectId,
                                      FieldDataType dataType,
                                      Function<AdditionalFieldValueReadable, T> handler,
                                      ContextTransaction<?> context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        validateAdditionalField(additionalFieldId, EnumSet.of(dataType), transaction);
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId);
        List<AdditionalFieldValueReadable> values = additionalFieldValueReadableResource.getAll(filter, transaction);
        values.sort(Comparator.comparingInt(AdditionalFieldValueReadable::getIndex));
        return values.stream().map(handler).collect(Collectors.toCollection(ArrayList::new));
    }

    private void validateAdditionalField(long additionalFieldId,
                                         EnumSet<FieldDataType> dataTypes,
                                         QueryTransaction transaction) throws PlatformException {
        AdditionalFieldReadable additionalField = new PrimaryKeyValidator(false)
                .validateAndGet(additionalFieldId, additionalFieldReadableResource, transaction);
        if (!dataTypes.contains(additionalField.getDataType())) {
            throw CoreExceptionBuilder.buildInvalidFieldDataTypeException();
        }
    }
}
