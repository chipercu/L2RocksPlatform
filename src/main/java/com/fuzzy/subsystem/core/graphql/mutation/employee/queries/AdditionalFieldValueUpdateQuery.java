package com.fuzzy.subsystem.core.graphql.mutation.employee.queries;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.context.ContextTransaction;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.additionalfield.AdditionalFieldReadable;
import com.fuzzy.subsystem.core.domainobject.employee.EmployeeReadable;
import com.fuzzy.subsystem.core.employeeaccess.ManagerEmployeeAccessGetter;
import com.fuzzy.subsystem.core.enums.FieldDataType;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.graphql.query.employee.GEmployee;
import com.fuzzy.subsystem.core.remote.additionalfieldvaluesetter.RCAdditionalFieldValueSetter;
import com.fuzzy.subsystem.core.remote.integrations.RCIntegrationsExecutor;
import com.fuzzy.subsystems.exception.GeneralExceptionBuilder;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

public abstract class AdditionalFieldValueUpdateQuery<T> extends GraphQLQuery<RemoteObject, GEmployee> {

    private final long employeeId;
    private final long additionalFieldId;
    private final T value;
    private final FieldDataType dataType;

    private ReadableResource<EmployeeReadable> employeeReadableResource;
    private ReadableResource<AdditionalFieldReadable> additionalFieldReadableResource;
    private RCIntegrationsExecutor rcIntegrations;
    private RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter;
    private ManagerEmployeeAccessGetter managerEmployeeAccessGetter;

    public AdditionalFieldValueUpdateQuery(long employeeId,
                                           long additionalFieldId,
                                           T value,
                                           FieldDataType dataType) {
        this.employeeId = employeeId;
        this.additionalFieldId = additionalFieldId;
        this.value = value;
        this.dataType = dataType;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        employeeReadableResource = resources.getReadableResource(EmployeeReadable.class);
        additionalFieldReadableResource = resources.getReadableResource(AdditionalFieldReadable.class);
        rcIntegrations = new RCIntegrationsExecutor(resources);
        rcAdditionalFieldValueSetter = resources.getQueryRemoteController(CoreSubsystem.class, RCAdditionalFieldValueSetter.class);
        managerEmployeeAccessGetter = new ManagerEmployeeAccessGetter(resources);
    }

    @Override
    public GEmployee execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        PrimaryKeyValidator pkValidator = new PrimaryKeyValidator(false);
        EmployeeReadable employee = pkValidator.validateAndGet(employeeId, employeeReadableResource, transaction);
        if (!managerEmployeeAccessGetter.getAccess(context).checkEmployee(employeeId)) {
            throw GeneralExceptionBuilder.buildAccessDeniedException();
        }
        AdditionalFieldReadable field = pkValidator.validateAndGet(additionalFieldId, additionalFieldReadableResource, transaction);
        if (field.getDataType() != dataType) {
            throw CoreExceptionBuilder.buildInvalidFieldDataTypeException();
        }
        if (rcIntegrations.isSynchronized(employeeId, field.getObjectType(), field.getKey(), context)) {
            throw CoreExceptionBuilder.buildSynchronizedFieldException();
        }
        setValue(rcAdditionalFieldValueSetter, additionalFieldId, employeeId, value, context);
        return new GEmployee(employee);
    }

    protected abstract void setValue(RCAdditionalFieldValueSetter rcAdditionalFieldValueSetter,
                                     long additionalFieldId,
                                     long objectId,
                                     T value,
                                     ContextTransaction<?> context) throws PlatformException;
}
