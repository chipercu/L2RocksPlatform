package com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter;

import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.*;
import com.fuzzy.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueEditable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.function.Consumer;
import com.fuzzy.subsystems.remote.RCExecutor;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class RCAdditionalFieldValueSetterImpl extends AbstractQueryRController<CoreSubsystem> implements RCAdditionalFieldValueSetter {

    private final ReadableResource<EmployeeReadable> employeeReadableResource;
    private final ReadableResource<DepartmentReadable> departmentReadableResource;
    private final ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private final RemovableResource<AdditionalFieldValueEditable> additionalFieldValueRemovableResource;
    private final RCExecutor<RCAdditionalFieldValueNotifications> rcNotifications;

    public RCAdditionalFieldValueSetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        additionalFieldValueRemovableResource = resources.getRemovableResource(AdditionalFieldValueEditable.class);
        rcNotifications = new RCExecutor<>(resources, RCAdditionalFieldValueNotifications.class);
    }

    @Override
    public int addLongValue(long additionalFieldId, long objectId, Long value, @NonNull ContextTransaction context) throws PlatformException {
        return addValue(additionalFieldId, objectId, additionalFieldValue -> setLongValue(additionalFieldValue, value),
                FieldDataType.LONG, FieldDataType.LONG_ARRAY, context);
    }

    @Override
    public int addStringValue(long additionalFieldId, long objectId, String value, @NonNull ContextTransaction context) throws PlatformException {
        return addValue(additionalFieldId, objectId, additionalFieldValue -> setStringValue(additionalFieldValue, value),
                FieldDataType.STRING, FieldDataType.STRING_ARRAY, context);
    }

    @Override
    public int addDateValue(long additionalFieldId, long objectId, LocalDate value, @NonNull ContextTransaction context) throws PlatformException {
        return addValue(additionalFieldId, objectId, additionalFieldValue -> setDateValue(additionalFieldValue, value),
                FieldDataType.DATE, FieldDataType.DATE_ARRAY, context);
    }

    @Override
    public int addDateTimeValue(long additionalFieldId, long objectId, Instant value, @NonNull ContextTransaction context) throws PlatformException {
        return addValue(additionalFieldId, objectId, additionalFieldValue -> setDateTimeValue(additionalFieldValue, value),
                FieldDataType.DATETIME, FieldDataType.DATETIME_ARRAY, context);
    }

    @Override
    public void setLongValue(long additionalFieldId, long objectId, int index, Long value, @NonNull ContextTransaction context) throws PlatformException {
        setValue(additionalFieldId, objectId, index, additionalFieldValue -> setLongValue(additionalFieldValue, value),
                FieldDataType.LONG, FieldDataType.LONG_ARRAY, context);
    }

    @Override
    public void setIdValue(long additionalFieldId, long objectId, int index, Long value, @NonNull ContextTransaction context) throws PlatformException {
        final Consumer<AdditionalFieldReadable> validator = additionalFieldReadable -> {
            if (value == null) {
                return;
            }

            if (EmployeeReadable.class.getName().equals(additionalFieldReadable.getListSource())) {
                final EmployeeReadable employeeReadable = employeeReadableResource.get(value, context.getTransaction());
                if (employeeReadable == null) {
                    throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(EmployeeReadable.class, value);
                }
            } else if (DepartmentReadable.class.getName().equals(additionalFieldReadable.getListSource())) {
                if (value == -1) {
                    return;
                }

                final DepartmentReadable departmentReadable = departmentReadableResource.get(value, context.getTransaction());
                if (departmentReadable == null) {
                    throw GeneralExceptionBuilder.buildNotFoundDomainObjectException(DepartmentReadable.class, value);
                }
            } else {
                throw GeneralExceptionBuilder.buildInvalidValueException(AdditionalFieldReadable.class, AdditionalFieldReadable.FIELD_LIST_SOURCE, additionalFieldReadable.getListSource());
            }
        };
        setValue(additionalFieldId, objectId, index, validator, additionalFieldValue -> setLongValue(additionalFieldValue, value),
                FieldDataType.ID, FieldDataType.LONG_ARRAY, context);
    }

    @Override
    public void setStringValue(long additionalFieldId, long objectId, int index, String value, @NonNull ContextTransaction context) throws PlatformException {
        setValue(additionalFieldId, objectId, index, additionalFieldValue -> setStringValue(additionalFieldValue, value),
                FieldDataType.STRING, FieldDataType.STRING_ARRAY, context);
    }

    @Override
    public void setDateValue(long additionalFieldId, long objectId, int index, LocalDate value, @NonNull ContextTransaction context) throws PlatformException {
        setValue(additionalFieldId, objectId, index, additionalFieldValue -> setDateValue(additionalFieldValue, value),
                FieldDataType.DATE, FieldDataType.DATE_ARRAY, context);
    }

    @Override
    public void setDateTimeValue(long additionalFieldId, long objectId, int index, Instant value, @NonNull ContextTransaction context) throws PlatformException {
        setValue(additionalFieldId, objectId, index, additionalFieldValue -> setDateTimeValue(additionalFieldValue, value),
                FieldDataType.DATETIME, FieldDataType.DATETIME_ARRAY, context);
    }

    @Override
    public void removeValue(long additionalFieldId, long objectId, int index, @NonNull ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        AdditionalFieldReadable additionalField = getAdditionalField(additionalFieldId, transaction);
        switch (additionalField.getDataType()) {
            case STRING_ARRAY:
            case LONG_ARRAY:
            case DATE_ARRAY:
            case DATETIME_ARRAY:
                break;
            default:
                throw CoreExceptionBuilder.buildInvalidFieldDataTypeException();
        }
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId)
                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, index);
        AdditionalFieldValueEditable additionalFieldValue = additionalFieldValueRemovableResource.find(filter, transaction);
        if (additionalFieldValue == null) {
            throw CoreExceptionBuilder.buildFieldArrayItemNotFoundException();
        }
        final AdditionalFieldValueEditable beforeRemoveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onBeforeRemoveArrayValue(beforeRemoveAdditionalFieldValue, context));
        additionalFieldValueRemovableResource.remove(additionalFieldValue, transaction);
    }

    @Override
    public void removeValue(long additionalFieldId, long objectId, @NonNull ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        getAdditionalField(additionalFieldId, transaction);
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId);
        rcNotifications.exec(rc -> rc.onBeforeRemoveValue(additionalFieldId, objectId, context));
        additionalFieldValueRemovableResource.removeAll(filter, transaction);
    }

    private int addValue(long additionalFieldId,
                         long objectId,
                         Consumer<AdditionalFieldValueEditable> handler,
                         FieldDataType dataType,
                         FieldDataType arrayDataType,
                         ContextTransaction context) throws PlatformException {
        AdditionalFieldReadable additionalField = getAdditionalField(additionalFieldId, context.getTransaction());
        if (additionalField.getDataType() == dataType) {
            setValue(additionalFieldId, objectId, handler, context);
            return 0;
        } else if (additionalField.getDataType() == arrayDataType) {
            return addArrayItem(additionalFieldId, objectId, handler, context);
        } else {
            throw CoreExceptionBuilder.buildInvalidFieldDataTypeException();
        }
    }

    private void setValue(long additionalFieldId,
                          long objectId,
                          int index,
                          Consumer<AdditionalFieldValueEditable> handler,
                          FieldDataType dataType,
                          FieldDataType arrayDataType,
                          ContextTransaction context) throws PlatformException {
        setValue(additionalFieldId, objectId, index, additionalFieldReadable -> {}, handler, dataType, arrayDataType, context);
    }

    private void setValue(long additionalFieldId,
                          long objectId,
                          int index,
                          Consumer<AdditionalFieldReadable> validation,
                          Consumer<AdditionalFieldValueEditable> handler,
                          FieldDataType dataType,
                          FieldDataType arrayDataType,
                          ContextTransaction context) throws PlatformException {
        AdditionalFieldReadable additionalField = getAdditionalField(additionalFieldId, context.getTransaction());
        validation.accept(additionalField);
        if (additionalField.getDataType() == dataType) {
            setValue(additionalFieldId, objectId, handler, context);
        } else if (additionalField.getDataType() == arrayDataType) {
            setArrayItem(additionalFieldId, objectId, index, handler, context);
        } else {
            throw CoreExceptionBuilder.buildInvalidFieldDataTypeException();
        }
    }

    private void setValue(long additionalFieldId,
                          long objectId,
                          Consumer<AdditionalFieldValueEditable> handler,
                          ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId)
                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, 0);
        AdditionalFieldValueEditable additionalFieldValue = additionalFieldValueRemovableResource.find(filter, transaction);
        if (additionalFieldValue == null) {
            additionalFieldValue = additionalFieldValueRemovableResource.create(transaction);
            additionalFieldValue.setAdditionalFieldId(additionalFieldId);
            additionalFieldValue.setObjectId(objectId);
            additionalFieldValue.setIndex(0);
        }
        handler.accept(additionalFieldValue);
        final AdditionalFieldValueEditable beforeSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onBeforeSetValue(beforeSaveAdditionalFieldValue, context));
        additionalFieldValueRemovableResource.save(additionalFieldValue, transaction);
        final AdditionalFieldValueEditable afterSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onAfterSetValue(afterSaveAdditionalFieldValue, context));
    }

    private int addArrayItem(long additionalFieldId,
                             long objectId,
                             Consumer<AdditionalFieldValueEditable> handler,
                             ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId);
        int[] index = new int[]{-1};
        additionalFieldValueRemovableResource.forEach(filter, additionalFieldValue -> {
            if (additionalFieldValue.getIndex() > index[0]) {
                index[0] = additionalFieldValue.getIndex();
            }
        }, transaction);
        index[0]++;
        AdditionalFieldValueEditable additionalFieldValue = additionalFieldValueRemovableResource.create(transaction);
        additionalFieldValue.setAdditionalFieldId(additionalFieldId);
        additionalFieldValue.setObjectId(objectId);
        additionalFieldValue.setIndex(index[0]);
        handler.accept(additionalFieldValue);
        final AdditionalFieldValueEditable beforeSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onBeforeAddArrayItem(beforeSaveAdditionalFieldValue, context));
        additionalFieldValueRemovableResource.save(additionalFieldValue, transaction);
        final AdditionalFieldValueEditable afterSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onAfterAddArrayItem(afterSaveAdditionalFieldValue, context));
        return index[0];
    }

    private void setArrayItem(long additionalFieldId,
                              long objectId,
                              int index,
                              Consumer<AdditionalFieldValueEditable> handler,
                              ContextTransaction context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        HashFilter filter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalFieldId)
                .appendField(AdditionalFieldValueReadable.FIELD_OBJECT_ID, objectId)
                .appendField(AdditionalFieldValueReadable.FIELD_INDEX, index);
        AdditionalFieldValueEditable additionalFieldValue = additionalFieldValueRemovableResource.find(filter, transaction);
        if (additionalFieldValue == null) {
            throw CoreExceptionBuilder.buildFieldArrayItemNotFoundException();
        }
        handler.accept(additionalFieldValue);
        final AdditionalFieldValueEditable beforeSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onBeforeSetArrayItem(beforeSaveAdditionalFieldValue, context));
        additionalFieldValueRemovableResource.save(additionalFieldValue, transaction);
        final AdditionalFieldValueEditable afterSaveAdditionalFieldValue = additionalFieldValue;
        rcNotifications.exec(rc -> rc.onAfterSetArrayItem(afterSaveAdditionalFieldValue, context));
    }

    private AdditionalFieldReadable getAdditionalField(long additionalFieldId, QueryTransaction transaction) throws PlatformException {
        return new PrimaryKeyValidator(false)
                .validateAndGet(additionalFieldId, additionalFieldReadableResource, transaction);
    }

    private void setLongValue(AdditionalFieldValueEditable additionalFieldValue, Long value) {
        if (!Objects.equals(additionalFieldValue.getLongValue(), value)) {
            additionalFieldValue.setLongValue(value);
        }
    }

    private void setStringValue(AdditionalFieldValueEditable additionalFieldValue, String value) {
        if (!Objects.equals(additionalFieldValue.getStringValue(), value)) {
            additionalFieldValue.setStringValue(value);
        }
    }

    private void setDateValue(AdditionalFieldValueEditable additionalFieldValue, LocalDate value) {
        if (!Objects.equals(additionalFieldValue.getDateValue(), value)) {
            additionalFieldValue.setDateValue(value);
        }
    }

    private void setDateTimeValue(AdditionalFieldValueEditable additionalFieldValue, Instant value) {
        if (!Objects.equals(additionalFieldValue.getDateTimeValue(), value)) {
            additionalFieldValue.setDateTimeValue(value);
        }
    }
}
