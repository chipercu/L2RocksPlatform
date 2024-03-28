package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.subscriber;

import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.session.TransportSession;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.packet.Packet;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.packet.TypePacket;
import com.fuzzy.main.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import net.minidev.json.JSONObject;

import java.io.Serializable;

public class WebSocketGraphQLWSSubscriber extends WebSocketSubscriber {

    public WebSocketGraphQLWSSubscriber(GraphQLSubscriber graphQLSubscriber, Serializable packetId, TransportSession transportSession) {
        super(graphQLSubscriber, packetId, transportSession);
    }

    @Override
    public IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse) {
        return new Packet(
                (String) packetId,
                TypePacket.GQL_DATA,
                nextGraphQLResponse.data
        );
    }

}
