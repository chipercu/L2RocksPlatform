package com.fuzzy.main.network.protocol.standard.session;

import com.fuzzy.main.network.exception.ParsePacketNetworkException;
import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.protocol.standard.StandardProtocol;
import com.fuzzy.main.network.protocol.standard.packet.Packet;
import com.fuzzy.main.network.protocol.standard.packet.ResponsePacket;
import com.fuzzy.main.network.session.SessionImpl;
import com.fuzzy.main.network.session.TransportSession;
import com.fuzzy.main.network.struct.HandshakeData;
import com.fuzzy.main.network.transport.Transport;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 17.09.13
 * Time: 22:44
 */
public class StandardTransportSession extends TransportSession {

    public final static long DEFAULT_REQUEST_TIMEOUT = 3L * 60L * 1000L;//Таймаут

    private final static Logger log = LoggerFactory.getLogger(StandardTransportSession.class);

    /**
     * итератор для id пакетов- запрашиваем о чем то клиент
     */
    private final AtomicLong nextIdPacketToQuestionClient = new AtomicLong(-1);
    private final Map<Long, CompletableFuture<ResponsePacket>> waitResponses = new ConcurrentHashMap<Long, CompletableFuture<ResponsePacket>>();

    //Флаг определяеющий что мы в фазе рукопожатия
    private volatile boolean phaseHandshake;

    public StandardTransportSession(
            StandardProtocol protocol,
            final Transport transport,
            final Object channel
    ) {
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
    public boolean isPhaseHandshake() {
        return phaseHandshake;
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
        ((SessionImpl) session).initHandshakeData(handshakeData);
//		network.onHandshake(session);
    }

    @Override
    public void failPhaseHandshake(IPacket packet) {
        ResponsePacket responsePacket = (ResponsePacket) packet;
        try {
            if (responsePacket != null) {
                transport.send(channel, responsePacket);
            }
            transport.close(channel);
        } catch (Throwable ignore) {
        }
        destroyed();
    }


    /**
     * Возврощаем обработчика пакетов
     *
     * @return
     */
    public PacketHandler getPacketHandler() {
        StandardProtocol standardProtocol = (StandardProtocol) protocol;
        if (phaseHandshake) {
            return standardProtocol.handshake;
        } else {
            return standardProtocol.getPacketHandler();
        }
    }
}