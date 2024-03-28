package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqltransportws;

import com.fuzzy.main.network.exception.NetworkException;
import com.fuzzy.main.network.protocol.Protocol;
import com.fuzzy.main.network.protocol.ProtocolBuilder;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.graphql.GraphQLTransportWSHandler;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.DefaultHandshake;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Handshake;

public class GraphqlTransportWSProtocolBuilder extends ProtocolBuilder {

    private final GraphQLTransportWSHandler packetHandler;

    private Handshake handshake;

    public GraphqlTransportWSProtocolBuilder(GraphQLTransportWSHandler packetHandler) {
        this.packetHandler = packetHandler;
        this.handshake = new DefaultHandshake();
    }

    public GraphqlTransportWSProtocolBuilder withHandshake(Handshake handshake) {
        this.handshake = handshake;
        return this;
    }

    @Override
    public Protocol build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException {
        return new GraphqlTransportWSProtocol(handshake, packetHandler, uncaughtExceptionHandler);
    }
}
