package com.fuzzy.main.platform.component.frontend.engine.network.protocol.standard.subscriber;

import com.fuzzy.main.network.mvc.ResponseEntity;
import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.protocol.standard.packet.RequestPacket;
import com.fuzzy.main.network.protocol.standard.packet.ResponsePacket;
import com.fuzzy.main.network.protocol.standard.session.StandardTransportSession;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.GraphQLSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.network.subscriber.WebSocketSubscriber;
import com.fuzzy.main.platform.component.frontend.engine.service.graphqlrequestexecute.struct.GraphQLResponse;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Ulitin V. Не решена проблема! Если соединение сбрасывается по инициативе transportSession,
 * то у нас остается живая подписка, до первого нового события!
 */
public class WebSocketStandardSubscriber extends WebSocketSubscriber {

    private final static Logger log = LoggerFactory.getLogger(WebSocketStandardSubscriber.class);

    public WebSocketStandardSubscriber(GraphQLSubscriber graphQLSubscriber, StandardTransportSession transportSession, RequestPacket requestPacket) {
        super(graphQLSubscriber, requestPacket.getId(), transportSession);
    }

    @Override
    public IPacket buildPacket(GraphQLResponse<JSONObject> nextGraphQLResponse) {
        ResponseEntity responseEntity = (nextGraphQLResponse.error)
                ? ResponseEntity.error(nextGraphQLResponse.data)
                : ResponseEntity.success(nextGraphQLResponse.data);

        return new ResponsePacket(
                (long) packetId,
                responseEntity.code,
                responseEntity.data);
    }

}
