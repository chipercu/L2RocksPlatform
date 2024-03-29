package com.fuzzy.subsystem.core.graphql.query.systemevent;


import com.fuzzy.main.cluster.core.remote.struct.RemoteObject;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLDescription;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLField;
import com.fuzzy.main.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.fuzzy.main.platform.component.frontend.context.ContextTransactionRequest;
import com.fuzzy.main.platform.querypool.ResourceProvider;
import com.fuzzy.main.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.main.platform.sdk.graphql.customfield.graphqlquery.GraphQLQuery;
import com.fuzzy.subsystem.core.CoreSubsystem;
import com.fuzzy.subsystem.core.service.systemevent.SystemEventService;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@GraphQLTypeOutObject("system_event_query")
public class GQuerySystemEvent {

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Список уведомлений системы")
    public static GraphQLQuery<RemoteObject, ArrayList<GSystemEvent>> getEvents(
            CoreSubsystem coreSubsystem
    ) {
        return new GraphQLQuery<>() {

            private SystemEventService systemEventService;

            @Override
            public void prepare(ResourceProvider resources) {
                systemEventService = coreSubsystem.getSystemEventService();
            }

            @Override
            public ArrayList<GSystemEvent> execute(RemoteObject source, ContextTransactionRequest context) {
                final ZonedDateTime now = ZonedDateTime.now();
                return systemEventService.getActualEvents(now)
                        .stream()
                        .map(GSystemEvent::new)
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        };
    }
}