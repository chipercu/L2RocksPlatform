package com.fuzzy.subsystem.extensions.network;

import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public interface IPacketHandler<T extends MMOClient> {
    public ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}