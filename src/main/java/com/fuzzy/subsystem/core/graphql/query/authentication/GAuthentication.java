package com.fuzzy.subsystem.core.graphql.query.authentication;

import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLName;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.domainobject.authentication.AuthenticationReadable;
import com.fuzzy.subsystem.core.graphql.query.authentication.queries.EmployeeCountQuery;
import com.fuzzy.subsystem.core.remote.authentication.RCAuthenticationContent;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystems.graphql.GDomainObject;
import com.fuzzy.subsystems.graphql.input.GStandardFilter;
import com.fuzzy.subsystems.graphql.out.GCount;
import com.fuzzy.subsystems.remote.RCExecutor;

@GraphQLTypeOutObject("authentication")
public class GAuthentication extends GDomainObject<AuthenticationReadable> {

    private static final String EMPLOYEE_FILTER = "employee_filter";

    private final GAuthenticationType type;

    public GAuthentication(AuthenticationReadable source) {
        super(source);
        type = new GAuthenticationType(source.getType());
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Идентификатор")
    public long getId() {
        return super.getSource().getId();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Название")
    public String getName() {
        return getSource().getName();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тип")
    public GAuthenticationType getType() {
        return type;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Контент")
    public static GraphQLQuery<GAuthentication, GAuthenticationContent> getContent() {
        return new GraphQLQuery<GAuthentication, GAuthenticationContent>() {

            private RCExecutor<RCAuthenticationContent> rcAuthenticationContent;

            @Override
            public void prepare(ResourceProvider resources) {
                rcAuthenticationContent = new RCExecutor<>(resources, RCAuthenticationContent.class);
            }

            @Override
            public GAuthenticationContent execute(GAuthentication source,
                                                  ContextTransactionRequest context) throws PlatformException {
                AuthenticationReadable authentication = source.getSource();
                return rcAuthenticationContent.getFirstNotNull(rc ->
                        rc.getContent(authentication.getId(), authentication.getType(), context));
            }
        };
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Количество сотрудников с аутентификацией")
    public static GraphQLQuery<GAuthentication, GCount> getEmployeeCount(
            @GraphQLDescription("Фильтр по сотрудникам")
            @GraphQLName(EMPLOYEE_FILTER) final GStandardFilter employeeFilter
    ) {
        return new EmployeeCountQuery(employeeFilter);
    }
}
