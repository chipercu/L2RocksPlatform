package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake;

import com.fuzzy.main.network.struct.HandshakeData;
import com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.packet.Packet;

public class DefaultHandshake extends Handshake {

    @Override
    public HandshakeData handshake(Packet packet) {
        return null;
    }
}
