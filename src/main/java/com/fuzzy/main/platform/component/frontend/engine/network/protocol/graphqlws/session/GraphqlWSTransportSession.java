package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.session;

import com.fuzzy.main.network.exception.ParsePacketNetworkException;
import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.session.SessionImpl;
import com.fuzzy.main.network.session.TransportSession;
import com.fuzzy.main.network.struct.HandshakeData;
import com.fuzzy.main.network.transport.Transport;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.GraphqlWSProtocol;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.packet.Packet;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class GraphqlWSTransportSession extends TransportSession {

    private final static Logger log = LoggerFactory.getLogger(GraphqlWSTransportSession.class);

    //Флаг определяеющий что мы в фазе рукопожатия
    private volatile boolean phaseHandshake;

    public GraphqlWSTransportSession(GraphqlWSProtocol protocol, final Transport transport, final Object channel) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(protocol, transport, channel);

        //Проверяем наличие фазы рукопожатия
        if (protocol.handshake != null) {
            phaseHandshake = true;
        } else {
            phaseHandshake = false;
//			network.onHandshake(session);
        }
    }

    @Override
    public IPacket parse(String message) throws ParsePacketNetworkException {
        try {
            JSONObject incoming = (JSONObject) new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(message);
            return Packet.parse(incoming);
        } catch (ParseException e) {
            throw new ParsePacketNetworkException(e);
        }
    }


    @Override
    public void completedPhaseHandshake(HandshakeData handshakeData) {
        phaseHandshake = false;
        ((SessionImpl)session).initHandshakeData(handshakeData);
    }

    @Override
    public void failPhaseHandshake(IPacket responsePacket) {

    }

    @Override
    public boolean isPhaseHandshake() {
        return phaseHandshake;
    }

    /**
     * Возврощаем обработчика пакетов
     *
     * @return
     */
    @Override
    public PacketHandler getPacketHandler() {
        GraphqlWSProtocol graphqlWSProtocol = (GraphqlWSProtocol) protocol;
        if (phaseHandshake) {
            return graphqlWSProtocol.handshake;
        } else {
            return graphqlWSProtocol.packetHandler;
        }
    }
}
