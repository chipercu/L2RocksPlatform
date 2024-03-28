package com.fuzzy.main.network.transport;

import com.fuzzy.main.network.exception.ParsePacketNetworkException;
import com.fuzzy.main.network.packet.IPacket;
import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.session.Session;

public interface TransportPacketHandler {

    boolean isPhaseHandshake();

    PacketHandler getPacketHandler();

    Session getSession();

    IPacket parse(String message) throws ParsePacketNetworkException;

    void send(IPacket packet);

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();
}
