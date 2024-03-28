package com.fuzzy.main.network.protocol.standard;

import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.protocol.Protocol;
import com.fuzzy.main.network.protocol.standard.handler.handshake.Handshake;
import com.fuzzy.main.network.protocol.standard.session.StandardTransportSession;
import com.fuzzy.main.network.session.Session;
import com.fuzzy.main.network.transport.Transport;

public class StandardProtocol extends Protocol {

    public static final String NAME="ws-mvc";

    public final Handshake handshake;

    private final PacketHandler packetHandler;

    protected StandardProtocol(
            Handshake handshake,
            PacketHandler packetHandler,
            Thread.UncaughtExceptionHandler uncaughtExceptionHandler
    ) {
        super(uncaughtExceptionHandler);

        this.handshake = handshake;
        this.packetHandler = packetHandler;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public StandardTransportSession onConnect(Transport transport, Object channel) throws Exception {

        StandardTransportSession transportSession = new StandardTransportSession(this, transport, channel);

        //Начинаем фазу рукопожатия
        if (handshake == null) {
            //У нас нет обработчика рукопожатий, сразу считаем что оно свершилось)
            onHandshake(transportSession.getSession());
        } else {
            handshake.onPhaseHandshake(transportSession.getSession());
        }

        return transportSession;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void onHandshake(Session session) {
        //Оповещаем подписчиков
//        for (NetworkListener listener : listeners) {
//            listener.onHandshake(session);
//        }
    }

}