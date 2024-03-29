package com.fuzzy.subsystem.core.remote.fieldsgetter;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.AbstractQueryRController;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.CoreSubsystemConsts;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RCFieldsGetterImpl extends AbstractQueryRController<CoreSubsystem> implements RCFieldsGetter {

    private final ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;

    public RCFieldsGetterImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
    }

    @Override
    public @NonNull ArrayList<SystemFieldDescription> getSystemFields(@NonNull String objectType, @NonNull ContextTransaction context) throws PlatformException {
        if (Objects.equals(objectType, EmployeeReadable.class.getName())) {
            ArrayList<SystemFieldDescription> fields = new ArrayList<>();
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.FIRST_NAME_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.PATRONYMIC_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.SECOND_NAME_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.LANGUAGE_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.DEPARTMENT_ID_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.PERSONNEL_NUMBER_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.EMAIL_KEY, FieldDataType.STRING));
            fields.add(new SystemFieldDescription(
                    CoreSubsystemConsts.EmployeeSystemFields.PHONE_NUMBER_KEY, FieldDataType.STRING_ARRAY));
            return fields;
        }
        return new ArrayList<>();
    }

    @Override
    public @NonNull ArrayList<AdditionalFieldDescription> getAdditionalFields(@NonNull String objectType,
                                                                              @NonNull ContextTransaction context) throws PlatformException {
        List<AdditionalFieldReadable> additionalFields = new ArrayList<>();
        HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, objectType);
        additionalFieldReadableResource.forEach(filter, additionalFields::add, context.getTransaction());
        additionalFields.sort(Comparator.comparingInt(AdditionalFieldReadable::getOrder));
        return additionalFields.stream()
                .map(AdditionalFieldDescription::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
