package com.fuzzy.subsystem.core.graphql.query.authentication.queries;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ReadableResource;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.filterhandler.AccessibleEmployeeFilterHandler;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthentication;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.out.GCount;

import java.util.Set;

public class EmployeeCountQuery extends GraphQLQuery<GAuthentication, GCount> {

    private final GStandardFilter employeeFilter;

    private ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;
    private AccessibleEmployeeFilterHandler employeeFilterHandler;

    public EmployeeCountQuery(GStandardFilter employeeFilter) {
        this.employeeFilter = employeeFilter;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
        employeeFilterHandler = new AccessibleEmployeeFilterHandler(resources);
    }

    @Override
    public GCount execute(GAuthentication source, ContextTransactionRequest context) throws PlatformException {
        Set<Long> employees = employeeFilterHandler.get(employeeFilter, context);
        int[] employeeWithAuthenticationCount = new int[]{ 0 };
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_AUTHENTICATION_ID, source.getId());
        employeeAuthenticationReadableResource.forEach(filter, employeeAuthentication -> {
            if (employees.contains(employeeAuthentication.getEmployeeId())) {
                employeeWithAuthenticationCount[0]++;
            }
        }, context.getTransaction());
        return new GCount(employeeWithAuthenticationCount[0], employees.size());
    }
}
