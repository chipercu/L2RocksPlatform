package com.fuzzy.subsystem.core.graphql.query.session;

import com.fuzzy.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.platform.exception.PlatformException;
import com.fuzzy.platform.querypool.ResourceProvider;
import com.fuzzy.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.graphql.query.systemnotification.GSystemNotification;
import com.fuzzy.subsystem.core.service.notification.message.SystemNotificationMessageGetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;
import com.fuzzy.subsystem.frontend.graphql.query.GSession;


@GraphQLTypeOutObject("session")
public class GCoreSession {


    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Сообщение информационного окна пользователя")
    public static GraphQLQuery<GSession, GSystemNotification> getSystemNotification(CoreSubsystem coreSubsystem) {
        return new GraphQLQuery<GSession, GSystemNotification>() {
            private SystemNotificationMessageGetter systemNotificationMessageGetter;

            @Override
            public void prepare(ResourceProvider resources) {
                systemNotificationMessageGetter = new SystemNotificationMessageGetter(coreSubsystem, resources);
            }

            @Override
            public GSystemNotification execute(GSession source, ContextTransactionRequest context) throws PlatformException {
                final Long employeeId = source.getAuthEmployeeId();
                String message = systemNotificationMessageGetter.getMessage(employeeId, context.getTransaction());
                if (message.isEmpty()) {
                    return null;
                }
                return new GSystemNotification(message);
            }
        };
    }
}
