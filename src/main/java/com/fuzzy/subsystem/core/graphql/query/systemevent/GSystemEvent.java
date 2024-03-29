package com.fuzzy.subsystem.core.graphql.query.systemevent;

import com.infomaximum.cluster.core.remote.struct.RemoteObject;
import com.infomaximum.cluster.graphql.anotation.GraphQLDescription;
import com.infomaximum.cluster.graphql.anotation.GraphQLField;
import com.infomaximum.cluster.graphql.anotation.GraphQLTypeOutObject;
import com.infomaximum.platform.sdk.graphql.annotation.GraphQLAuthControl;
import com.fuzzy.subsystem.core.service.systemevent.SystemEvent;
import com.fuzzy.subsystem.frontend.authcontext.AuthorizedContext;

import java.time.format.DateTimeFormatter;


@GraphQLTypeOutObject("system_event")
public class GSystemEvent implements RemoteObject {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS XXX");

    private final SystemEvent event;

    public GSystemEvent(SystemEvent event) {
        this.event = event;
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Тип события")
    public String getEventType() {
        return event.getEventType();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Время события")
    public String getTime() {
        return timeFormatter.format(event.getTime());
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Уровень логирования события")
    public String getLevel() {
        return event.getLevel().name();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Идентификатор подсистемы")
    public String getSubsystemUuid() {
        return event.getSubsystemUuid();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Сообщение")
    public String getMessage() {
        return event.getMessage();
    }

    @GraphQLField
    @GraphQLAuthControl({ AuthorizedContext.class })
    @GraphQLDescription("Время отображения сообщения после возникновения события.")
    public Long getTimeToLiveMs() {
        return event.getTtl().toMillis();
    }
}