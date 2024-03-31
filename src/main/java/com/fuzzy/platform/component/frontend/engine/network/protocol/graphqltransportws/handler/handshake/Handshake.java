package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake;

import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.protocol.PacketHandler;
import com.fuzzy.network.protocol.standard.packet.ResponsePacket;
import com.fuzzy.network.session.Session;
import com.fuzzy.network.session.SessionImpl;
import com.fuzzy.network.struct.HandshakeData;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Response;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.TypePacket;
import com.fuzzy.platform.exception.PlatformException;

import java.util.concurrent.CompletableFuture;

/**
 * Created by kris on 01.09.16.
 */
public abstract class Handshake implements PacketHandler {

    public Handshake() {
    }

    public void onPhaseHandshake(Session session) {

    }

    /**
     * Завершаем фазу рукопожатия
     *
     * @param session
     */
    public void completedPhaseHandshake(Session session, HandshakeData handshakeData) {
        ((SessionImpl)session).getTransportSession().completedPhaseHandshake(handshakeData);
    }

    /**
     * Ошибка фазы рукопожатия - разрываем соединение
     *
     * @param session
     */
    public void failPhaseHandshake(Session session, ResponsePacket responsePacket) {
        ((SessionImpl)session).getTransportSession().failPhaseHandshake(responsePacket);
    }

    public abstract com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Response handshake(Packet packet) throws PlatformException;

    @Override
    public CompletableFuture<IPacket[]> exec(Session session, IPacket packet) {
        Packet requestPacket = (Packet) packet;
        Packet responsePacket;
        if (requestPacket.type == TypePacket.GQL_CONNECTION_INIT) {
            try {
                Response handshakeResponse = handshake(requestPacket);
                HandshakeData handshakeData = handshakeResponse.handshakeData();
                completedPhaseHandshake(session, handshakeData);
                responsePacket = new Packet(requestPacket.id, TypePacket.GQL_CONNECTION_ACK, handshakeResponse.payload());
            } catch (PlatformException e) {
                responsePacket = new Packet(requestPacket.id, TypePacket.GQL_CONNECTION_ERROR);
            }
        } else {
            responsePacket = new Packet(requestPacket.id, TypePacket.GQL_CONNECTION_ERROR);
        }
        return CompletableFuture.completedFuture(new IPacket[]{ responsePacket });
    }

}
