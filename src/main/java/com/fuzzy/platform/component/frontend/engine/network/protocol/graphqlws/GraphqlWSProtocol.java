package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws;

import com.fuzzy.network.protocol.Protocol;
import com.fuzzy.network.transport.Transport;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.handler.graphql.GraphQLWSHandler;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake.Handshake;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.session.GraphqlWSTransportSession;

import java.lang.reflect.InvocationTargetException;

public class GraphqlWSProtocol extends Protocol {

    public final Handshake handshake;
    public final GraphQLWSHandler packetHandler;

    public GraphqlWSProtocol(Handshake handshake, GraphQLWSHandler packetHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(uncaughtExceptionHandler);
        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return "graphql-ws";
    }

    @Override
    public GraphqlWSTransportSession onConnect(Transport transport, Object channel) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception {

        GraphqlWSTransportSession transportSession = new GraphqlWSTransportSession(this, transport, channel);

        //Начинаем фазу рукопожатия
        if (handshake == null) {
            //Обработчика рукопожатий отсутсвует - сразу считаем что оно закончилось
//            onHandshake(transportSession.getSession());
        } else {
            handshake.onPhaseHandshake(transportSession.getSession());
        }

        return transportSession;
    }
}
