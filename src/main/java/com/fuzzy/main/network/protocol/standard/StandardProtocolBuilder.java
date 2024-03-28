package com.fuzzy.main.network.protocol.standard;

import com.fuzzy.main.network.exception.NetworkException;
import com.fuzzy.main.network.protocol.PacketHandler;
import com.fuzzy.main.network.protocol.Protocol;
import com.fuzzy.main.network.protocol.ProtocolBuilder;
import com.fuzzy.main.network.protocol.standard.handler.handshake.Handshake;

public class StandardProtocolBuilder extends ProtocolBuilder {

    private Handshake handshake = null;
    private PacketHandler.Builder packetHandlerBuilder = null;

    public StandardProtocolBuilder() {
    }

    public StandardProtocolBuilder withHandshake(Handshake handshake) {
        this.handshake = handshake;
        return this;
    }

    public StandardProtocolBuilder withPacketHandler(PacketHandler.Builder packetHandlerBuilder) {
        this.packetHandlerBuilder = packetHandlerBuilder;
        return this;
    }

    @Override
    public Protocol build(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) throws NetworkException {
        return new StandardProtocol(
                handshake,
                packetHandlerBuilder.build(uncaughtExceptionHandler),
                uncaughtExceptionHandler
        );
    }
}
