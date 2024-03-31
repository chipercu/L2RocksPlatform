package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake;

import com.fuzzy.network.struct.HandshakeData;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Handshake;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Response;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.packet.Packet;


public class DefaultHandshake extends Handshake {

    @Override
    public com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake.Response handshake(Packet packet) {
        return new Response(null, null);
    }
}
