package com.fuzzy.main.network.protocol.standard.handler.handshake;

import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.protocol.standard.packet.ResponsePacket;
import com.fuzzy.main.network.session.Session;
import com.fuzzy.main.network.session.SessionImpl;
import com.fuzzy.main.network.struct.HandshakeData;

/**
 * Created by kris on 01.09.16.
 */
public abstract class Handshake implements PacketHandler {

    public abstract void onPhaseHandshake(Session session);

    /**
     * Завершаем фазу рукопожатия
     *
     * @param session
     */
    public void completedPhaseHandshake(Session session, HandshakeData handshakeData) {
        ((SessionImpl) session).getTransportSession().completedPhaseHandshake(handshakeData);
    }

    /**
     * Ошибка фазы рукопожатия - разрываем соединение
     *
     * @param session
     */
    public void failPhaseHandshake(Session session, ResponsePacket responsePacket) {
        ((SessionImpl) session).getTransportSession().failPhaseHandshake(responsePacket);
    }
}
