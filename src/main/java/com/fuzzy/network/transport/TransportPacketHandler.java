package com.fuzzy.network.transport;

import com.fuzzy.network.exception.ParsePacketNetworkException;
import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.protocol.PacketHandler;
import com.fuzzy.network.session.Session;

public interface TransportPacketHandler {

    boolean isPhaseHandshake();

    PacketHandler getPacketHandler();

    Session getSession();

    IPacket parse(String message) throws ParsePacketNetworkException;

    void send(IPacket packet);

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();
}
