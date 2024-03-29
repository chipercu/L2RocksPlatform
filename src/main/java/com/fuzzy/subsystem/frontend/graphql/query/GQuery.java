package com.fuzzy.subsystem.frontend.graphql.query;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.authcontext.UnauthorizedContext;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
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
