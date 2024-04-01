package com.fuzzy.subsystem.extensions.network;

import java.net.InetAddress;
import java.nio.channels.SocketChannel;

public interface IAcceptFilter<T extends MMOClient> {

    boolean accept(SocketChannel sc);

    void incReceivablePacket(ReceivablePacket rp, T client);

    void incSendablePacket(SendablePacket sp, T client);

    void addConnect(T client);

    void removeConnect(T client);

    void BanIp(InetAddress address, int time, String comments);
}
