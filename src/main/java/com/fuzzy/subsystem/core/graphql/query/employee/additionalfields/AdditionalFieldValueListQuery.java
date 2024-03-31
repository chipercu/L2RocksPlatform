package com.fuzzy.subsystem.core.graphql.query.employee.additionalfields;

import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.department.DepartmentReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.graphql.query.department.GDepartment;
import com.fuzzy.subsystem.core.graphql.query.employee.GDepartmentEmployeeElement;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.remote.additionalfieldvaluegetter.RCAdditionalFieldValueGetter;
import com.fuzzy.subsystem.core.remote.fieldsgetter.AdditionalFieldDescription;
import com.fuzzy.subsystem.core.remote.fieldsgetter.RCFieldsGetter;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystems.remote.Optional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdditionalFieldValueListQuery extends GraphQLQuery<GEmployee, ArrayList<GFieldValueInterface>> {

    private ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private RCFieldsGetter rcFieldsGetter;
    private RCAdditionalFieldValueGetter rcAdditionalFieldValueGetter;
    private RCIntegrationsExecutor rcIntegrations;

    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private ReadableResource<DepartmentReadable> departmentReadableResource;

    @Override
    public void prepare(ResourceProvider resources) {
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        rcFieldsGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCFieldsGetter.class);
        rcAdditionalFieldValueGetter = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalFieldValueGetter.class);
        rcIntegrations = new RCIntegrationsExecutor(resources);

        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        departmentReadableResource = resources.getReadableResource(DepartmentReadable.class);
    }

    @Override
    public ArrayList<GFieldValueInterface> execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
        List<AdditionalFieldDescription> fields =
                rcFieldsGetter.getAdditionalFields(EmployeeReadable.class.getName(), context);
        ArrayList<GFieldValueInterface> fieldValueList = new ArrayList<>(fields.size());
        for (AdditionalFieldDescription field : fields) {
            fieldValueList.add(getFieldValue(field.getAdditionalFieldId(), source.getId(), context));
        }
        return fieldValueList;
    }

    private GFieldValueInterface getFieldValue(long fieldId,
                                               long employeeId,
                                               ContextTransactionRequest context) throws PlatformException {
        AdditionalFieldReadable field = additionalFieldReadableResource.get(fieldId, context.getTransaction());
        boolean isSynchronized = rcIntegrations.isSynchronized(
                employeeId, EmployeeReadable.class.getName(), field.getKey(), context);
        GFieldValueInterface fieldValue = null;
        switch (field.getDataType()) {
            case STRING:
                Optional<String> sValue =
                        rcAdditionalFieldValueGetter.getStringValue(fieldId, employeeId, 0, context);
                fieldValue = new GStringFieldValue(field, sValue.isPresent() ? sValue.get() : null, isSynchronized);
                break;
            case STRING_ARRAY:
                fieldValue = new GStringArrayFieldValue(field,
                        rcAdditionalFieldValueGetter.getStringArray(fieldId, employeeId, context), isSynchronized);
                break;
            case LONG:
                Optional<Long> lValue =
                        rcAdditionalFieldValueGetter.getLongValue(fieldId, employeeId, 0, context);
                fieldValue = new GLongFieldValue(field, lValue.isPresent() ? lValue.get() : null, isSynchronized);
                break;
            case LONG_ARRAY:
                fieldValue = new GLongArrayFieldValue(field,
                        rcAdditionalFieldValueGetter.getLongArray(fieldId, employeeId, context), isSynchronized);
                break;
            case DATE:
                Optional<LocalDate> dValue =
                        rcAdditionalFieldValueGetter.getDateValue(fieldId, employeeId, 0, context);
                fieldValue = new GDateFieldValue(field, dValue.isPresent() ? dValue.get() : null, isSynchronized);
                break;
            case DATE_ARRAY:
                fieldValue = new GDateArrayFieldValue(field,
                        rcAdditionalFieldValueGetter.getDateArray(fieldId, employeeId, context), isSynchronized);
                break;
            case DATETIME:
                Optional<Instant> dtValue =
                        rcAdditionalFieldValueGetter.getDateTimeValue(fieldId, employeeId, 0, context);
                fieldValue = new GDateTimeFieldValue(
                        field, dtValue.isPresent() ? dtValue.get() : null, ZoneOffset.UTC, isSynchronized);
                break;
            case DATETIME_ARRAY:
                fieldValue = new GDateTimeArrayFieldValue(field,
                        rcAdditionalFieldValueGetter.getDateTimeArray(fieldId, employeeId, context),
                        ZoneOffset.UTC,
                        isSynchronized);
                break;
            case ID:
                Optional<Long> idValue =
                        rcAdditionalFieldValueGetter.getIdValue(fieldId, employeeId, 0, context);
                GDepartmentEmployeeElement entityValue = null;
                if (idValue.isPresent() && idValue.get() != null) {
                    entityValue = getEntityValue(field, idValue.get(), context);
                }
                fieldValue = new GEntityFieldValue(field, idValue.get(), entityValue, isSynchronized);
                break;
        }
        return fieldValue;
    }

    private GDepartmentEmployeeElement getEntityValue(AdditionalFieldReadable field, long id, ContextTransactionRequest context) throws PlatformException {
        final String listSource = field.getListSource();
        if (Objects.equals(listSource, EmployeeReadable.class.getName())) {
            final EmployeeReadable employeeReadable = employeeReadableResource.get(id, context.getTransaction());
            if (employeeReadable == null) {
                return null;
            }
            return new GEmployee(employeeReadable);
        } else if (Objects.equals(listSource, DepartmentReadable.class.getName())) {
            final DepartmentReadable departmentReadable = departmentReadableResource.get(id, context.getTransaction());
            if (departmentReadable == null) {
                return null;
            }
            return new GDepartment(departmentReadable);
        }
        return null;
    }
}
