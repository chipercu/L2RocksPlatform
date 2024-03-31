package com.fuzzy.subsystem.core.graphql.mutation.employee.queries;

import com.fuzzy.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.database.domainobject.filter.HashFilter;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.QueryTransaction;
import com.fuzzy.platform.querypool.ReadableResource;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.mutation.employee.EmployeeFilterProcessor;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthenticationSettingQuery extends GraphQLQuery<RemoteObject, Boolean> {

    private final boolean targetAll;
    private final String targetAllParamName;
    private final HashSet<Long> targetDepartmentIds;
    private final HashSet<Long> targetEmployeeIds;
    private final HashSet<Long> authenticationIds;

    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;
    private RCEmployeeAuthentication rcEmployeeAuthentication;
    private EmployeeFilterProcessor employeeFilterProcessor;

    public AuthenticationSettingQuery(boolean targetAll,
                                      String targetAllParamName,
                                      HashSet<Long> targetDepartmentIds,
                                      HashSet<Long> targetEmployeeIds,
                                      HashSet<Long> authenticationIds) {
        this.targetAll = targetAll;
        this.targetAllParamName = targetAllParamName;
        this.targetDepartmentIds = targetDepartmentIds;
        this.targetEmployeeIds = targetEmployeeIds;
        this.authenticationIds = authenticationIds;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
        rcEmployeeAuthentication =
                resources.getQueryRemoteController(CoreSubsystem.class, RCEmployeeAuthentication.class);
        employeeFilterProcessor = new EmployeeFilterProcessor(resources);
    }

    @Override
    public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Set<Long> targetEmployees = employeeFilterProcessor.getEmployees(
                targetAll,
                targetAllParamName,
                targetDepartmentIds,
                targetEmployeeIds,
                context
        );
        Set<Long> validAuthenticationIds = new PrimaryKeyValidator(true).validate(
                authenticationIds, authenticationReadableResource, transaction);
        List<Long> erasingAuthentications = new ArrayList<>();
        for (Long targetEmployeeId : targetEmployees) {
            erasingAuthentications.clear();
            HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, targetEmployeeId);
            employeeAuthenticationReadableResource.forEach(filter, employeeAuthentication -> {
                long authenticationId = employeeAuthentication.getAuthenticationId();
                if (!validAuthenticationIds.contains(authenticationId)) {
                    erasingAuthentications.add(authenticationId);
                }
            }, transaction);
            for (Long authenticationId : validAuthenticationIds) {
                rcEmployeeAuthentication.assignAuthenticationToEmployee(authenticationId, targetEmployeeId, context);
            }
            for (Long authenticationId : erasingAuthentications) {
                rcEmployeeAuthentication.eraseAuthenticationForEmployee(authenticationId, targetEmployeeId, context);
            }
        }
        return true;
    }
}
