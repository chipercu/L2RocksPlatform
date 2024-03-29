package com.fuzzy.subsystem.core.graphql.query.employee;

import com.fuzzy.main.rdao.database.domainobject.filter.HashFilter;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.QueryTransaction;
import com.fuzzy.main.platform.querypool.ReadableResource;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.domainobject.employeeauthentication.EmployeeAuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.query.authentication.GAuthenticationCollection;
import com.fuzzy.subsystems.graphql.input.GPaging;
import com.fuzzy.subsystems.sorter.Sorter;
import com.fuzzy.subsystems.sorter.SorterUtil;

public class AuthenticationsQuery extends GraphQLQuery<GEmployee, GAuthenticationCollection> {

    private final GPaging paging;

    private ReadableResource<AuthenticationReadable> authenticationReadableResource;
    private ReadableResource<EmployeeAuthenticationReadable> employeeAuthenticationReadableResource;

    public AuthenticationsQuery(GPaging paging) {
        this.paging = paging;
    }

    @Override
    public void prepare(ResourceProvider resources) {
        authenticationReadableResource = resources.getReadableResource(AuthenticationReadable.class);
        employeeAuthenticationReadableResource = resources.getReadableResource(EmployeeAuthenticationReadable.class);
    }

    @Override
    public GAuthenticationCollection execute(GEmployee source, ContextTransactionRequest context) throws PlatformException {
        QueryTransaction transaction = context.getTransaction();
        Sorter<AuthenticationReadable> sorter = SorterUtil.getSorter(AuthenticationReadable::getName, paging);
        HashFilter filter = new HashFilter(EmployeeAuthenticationReadable.FIELD_EMPLOYEE_ID, source.getId());
        employeeAuthenticationReadableResource.forEach(filter, employeeAuthentication ->
                sorter.add(authenticationReadableResource.get(employeeAuthentication.getAuthenticationId(), transaction)), transaction);
        return new GAuthenticationCollection(sorter);
    }
}
