package com.fuzzy.subsystem.core.graphql.mutation.employee.queries;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter.RCAdditionalFieldValueSetter;

import java.util.List;

public abstract class AdditionalFieldArrayUpdateQuery<T> extends AdditionalFieldValueUpdateQuery<List<T>> {


    public AdditionalFieldArrayUpdateQuery(long employeeId, long additionalFieldId, List<T> value, FieldDataType dataType) {
        super(employeeId, additionalFieldId, value, dataType);
    }

    @Override
    protected final void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                  long additionalFieldId,
                                  long objectId,
                                  List<T> value,
                                  ContextTransaction<?> context) throws PlatformException {
        rcAdditionalFieldValueSetter.removeValue(additionalFieldId, objectId, context);
        if (value != null) {
            for (T item : value) {
                addItem(rcAdditionalFieldValueSetter, additionalFieldId, objectId, item, context);
            }
        }
    }

    protected abstract void addItem(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                    long additionalFieldId,
                                    long objectId,
                                    T item,
                                    ContextTransaction<?> context) throws PlatformException;
}