package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.subscriber;

import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.session.TransportSession;
import com.fuzzy.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.TypePacket;
import com.fuzzy.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;
import com.fuzzy.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import net.minidev.json.JSONObject;

import java.io.Serializable;

public class WebSocketGraphQLTransportWSSubscriber extends WebSocketSubscriber {

    public WebSocketGraphQLTransportWSSubscriber(GraphQLSubscriber graphQLSubscriber, Serializable packetId, TransportSession transportSession) {
        super(graphQLSubscriber, packetId, transportSession);
    }

    @Override
    public IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse) {
        return new Packet(
                (String) packetId,
                TypePacket.GQL_NEXT,
                nextGraphQLResponse.data
        );
    }

}
