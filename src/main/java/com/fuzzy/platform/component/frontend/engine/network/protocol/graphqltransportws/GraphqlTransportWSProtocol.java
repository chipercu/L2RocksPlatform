package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws;

import com.fuzzy.network.protocol.Protocol;
import com.fuzzy.network.session.TransportSession;
import com.fuzzy.network.transport.Transport;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.graphql.GraphQLTransportWSHandler;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Handshake;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.session.GraphqlTransportWSTransportSession;

import java.lang.reflect.InvocationTargetException;

public class GraphqlTransportWSProtocol extends Protocol {

    public final Handshake handshake;
    public final GraphQLTransportWSHandler packetHandler;

    public GraphqlTransportWSProtocol(Handshake handshake, GraphQLTransportWSHandler packetHandler, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        super(uncaughtExceptionHandler);

        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return "graphql-transport-ws";
    }

    @Override
    public TransportSession onConnect(Transport transport, Object channel) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, Exception {
        GraphqlTransportWSTransportSession transportSession = new GraphqlTransportWSTransportSession(this, transport, channel);

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
