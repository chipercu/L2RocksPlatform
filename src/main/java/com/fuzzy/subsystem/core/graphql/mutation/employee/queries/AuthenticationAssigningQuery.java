package com.fuzzy.subsystem.core.graphql.mutation.employee.queries;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.mutation.employee.EmployeeFilterProcessor;
import com.fuzzy.subsystem.core.remote.employeeauthentication.RCEmployeeAuthentication;
import com.fuzzy.subsystems.utils.PrimaryKeyValidator;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationAssigningQuery extends GraphQLQuery<RemoteObject, Boolean> {

    private final boolean targetAll;
    private final String targetAllParamName;
    private final HashSet<Long> targetDepartmentIds;
    private final HashSet<Long> targetEmployeeIds;
    private final HashSet<Long> erasedAuthenticationIds;
    private final HashSet<Long> assignedAuthenticationIds;

    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private RCEmployeeAuthentication rcEmployeeAuthentication;
    private EmployeeFilterProcessor employeeFilterProcessor;

    public AuthenticationAssigningQuery(boolean targetAll,
                                        String targetAllParamName,
                                        HashSet<Long> targetDepartmentIds,
                                        HashSet<Long> targetEmployeeIds,
                                        HashSet<Long> erasedAuthenticationIds,
                                        HashSet<Long> assignedAuthenticationIds) {
        this.targetAll = targetAll;
        this.targetAllParamName = targetAllParamName;
        this.targetDepartmentIds = targetDepartmentIds;
        this.targetEmployeeIds = targetEmployeeIds;
        this.erasedAuthenticationIds = erasedAuthenticationIds;
        this.assignedAuthenticationIds = assignedAuthenticationIds;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
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
        for (Long targetEmployeeId : targetEmployees) {
            PrimaryKeyValidator pkValidator = new PrimaryKeyValidator(true);
            for (Long authenticationId : erasedAuthenticationIds) {
                if (pkValidator.validate(authenticationId, authenticationReadableResource, transaction)) {
                    rcEmployeeAuthentication.eraseAuthenticationForEmployee(authenticationId, targetEmployeeId, context);
                }
            }
            for (Long authenticationId : assignedAuthenticationIds) {
                if (pkValidator.validate(authenticationId, authenticationReadableResource, transaction)) {
                    rcEmployeeAuthentication.assignAuthenticationToEmployee(authenticationId, targetEmployeeId, context);
                }
            }
        }
        return true;
    }
}
