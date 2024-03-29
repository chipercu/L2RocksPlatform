package com.fuzzy.subsystem.core.graphql.query.authentication.queries;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UsedAuthenticationTypesQuery extends GraphQLQuery<RemoteObject, ArrayList<GAuthenticationType>> {

    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;

    @Override
    public void prepare(ResourceProvider resources) {
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
    }

    @Override
    public ArrayList<GAuthenticationType> execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Set<String> usedAuthenticationTypes = new HashSet<>();
        authenticationReadableResource.forEach(authentication -> {
            if (!usedAuthenticationTypes.contains(authentication.getType())) {
                HashFilter filter =
                        new HashFilter(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, authentication.getId());
                if (employeeAuthenticationReadableResource.find(filter, transaction) != null) {
                    usedAuthenticationTypes.add(authentication.getType());
                }
            }
        }, transaction);
        return usedAuthenticationTypes.stream()
                .map(GAuthenticationType::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
