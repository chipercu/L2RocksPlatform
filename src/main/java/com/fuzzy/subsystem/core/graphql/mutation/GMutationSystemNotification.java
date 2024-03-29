package com.fuzzy.subsystem.core.graphql.mutation;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.component.frontend.context.ContextTransactionRequest;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.infomaximum.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.authcontext.employee.EmployeeAuthContext;
import com.fuzzy.subsystem.core.exception.CoreExceptionBuilder;
import com.fuzzy.subsystem.core.service.notification.message.SystemNotificationMessageSetter;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

@GraphQLTypeOutObject("mutation_system_notification")
public class GMutationSystemNotification {


    @GraphQLField
    @GraphQLAuthControl(AuthorizedContext.class)
    @GraphQLDescription("Мутация состояния отображения информационного окна")
    public static GraphQLQuery<RemoteObject, Boolean> confirmNotification(CoreSubsystem coreSubsystem) {
        return new GraphQLQuery<RemoteObject, Boolean>() {
            private SystemNotificationMessageSetter systemNotificationMessageSetter;

            @Override
            public void prepare(ResourceProvider resources) {
                systemNotificationMessageSetter = new SystemNotificationMessageSetter(coreSubsystem, resources);
            }

            @Override
            public Boolean execute(RemoteObject source, ContextTransactionRequest context) throws PlatformException {
                if (!(context.getSource().getAuthContext() instanceof EmployeeAuthContext)) {
                    throw CoreExceptionBuilder.buildInvalidAuthenticationTypeException();
                }
                long employeeId = ((EmployeeAuthContext) context.getSource().getAuthContext()).getEmployeeId();
                systemNotificationMessageSetter.blockMessage(employeeId, context.getTransaction());
                return true;
            }
        };
    }
}