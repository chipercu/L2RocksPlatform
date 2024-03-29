package com.fuzzy.subsystem.frontend.graphql.query;

import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.authcontext.UnauthorizedContext;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.exception.PlatformException;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeSessionAuthContext;
import com.fuzzy.subsystem.core.remote.logon.AuthStatus;
import com.fuzzy.subsystem.frontend.graphql.query.docs.GQueryDocs;

@GraphQLTypeOutObject("query")
public class GQuery {

    @GraphQLField
    @GraphQLAuthControl(UnauthorizedContext.class)
    @GraphQLDescription("Пользовательская сессия")
    public static GraphQLQuery<RemoteObject, GSession> getSession() {
        return new GraphQLQuery<RemoteObject, GSession>() {

            @Override
            public void prepare(ResourceProvider resources) {
            }

            @Override
            public GSession execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                UnauthorizedContext authContext = context.getSource().getAuthContext();
                if (authContext instanceof EmployeeSessionAuthContext) {
                    EmployeeSessionAuthContext authContextUser = (EmployeeSessionAuthContext) authContext;
                    return new GSession(authContextUser.getSessionId(), authContextUser.getEmployeeId(), AuthStatus.SUCCESS);
                }
                return null;
            }
        };
    }

    @GraphQLField
    @GraphQLDescription("Документация")
    public static Class<GQueryDocs> getDocs() {
        return GQueryDocs.class;
    }
}
