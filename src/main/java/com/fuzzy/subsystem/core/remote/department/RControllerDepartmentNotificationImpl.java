package com.fuzzy.subsystem.core.remote.department;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.RemovableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueEditable;
import com.fuzzy.subsystem.core.domainobject.additionalfieldvalue.AdditionalFieldValueReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.domainobject.managerdepartmentaccess.ManagerDepartmentAccessEditable;
import com.fuzzy.subsystem.core.enums.FieldDataType;

import java.util.Objects;

public class RControllerDepartmentNotificationImpl extends AbstractQueryRController<CoreSubsystem>
        implements RControllerDepartmentNotification {

    private final ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private final RemovableResource<AdditionalFieldValueEditable> additionalFieldValueRemovableResource;
    private final RemovableResource<ManagerDepartmentAccessEditable> managerDepartmentAccessEditableResource;

    public RControllerDepartmentNotificationImpl(CoreSubsystem component, ResourceProvider resources) {
        super(component, resources);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        additionalFieldValueRemovableResource = resources.getRemovableResource(AdditionalFieldValueEditable.class);
        managerDepartmentAccessEditableResource = resources.getRemovableResource(ManagerDepartmentAccessEditable.class);
    }

    @Override
    public void onBeforeUpdateDepartment(DepartmentReadable department, DepartmentBuilder changes, ContextTransaction context) {

    }

    @Override
    public void onBeforeRemoveDepartment(Long departmentId, ContextTransaction context) throws PlatformException {
        managerDepartmentAccessEditableResource.removeAll(new HashFilter(
                ManagerDepartmentAccessEditable.FIELD_DEPARTMENT_ID,
                departmentId),
                context.getTransaction()
        );

        HashFilter filter = new HashFilter(AdditionalFieldReadable.FIELD_OBJECT_TYPE, EmployeeReadable.class.getName());
        additionalFieldReadableResource.forEach(filter, additionalField -> {
            if (FieldDataType.ID == additionalField.getDataType() &&
                    Objects.equals(DepartmentReadable.class.getName(), additionalField.getListSource())) {

                HashFilter vFilter = new HashFilter(AdditionalFieldValueReadable.FIELD_ADDITIONAL_FIELD_ID, additionalField.getId())
                        .appendField(AdditionalFieldValueReadable.FIELD_LONG_VALUE, departmentId)
                        .appendField(AdditionalFieldValueReadable.FIELD_INDEX, 0);
                additionalFieldValueRemovableResource.removeAll(vFilter, context.getTransaction());
            }
        }, context.getTransaction());
    }

    @Override
    public void onAfterUpdateParentDepartment(Long departmentId, ContextTransaction context) throws PlatformException {

    }
}
