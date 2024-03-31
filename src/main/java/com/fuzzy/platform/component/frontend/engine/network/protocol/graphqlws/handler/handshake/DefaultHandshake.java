package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake;

import com.fuzzy.network.struct.HandshakeData;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.handler.handshake.Handshake;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.packet.Packet;

public class DefaultHandshake extends Handshake {

    @Override
    public HandshakeData handshake(Packet packet) {
        return null;
    }
}
