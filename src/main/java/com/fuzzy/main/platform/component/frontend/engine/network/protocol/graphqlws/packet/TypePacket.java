package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqlws.packet;

public enum TypePacket {

    GQL_CONNECTION_INIT("connection_init"),// Client -> Server
    GQL_CONNECTION_ACK("connection_ack"), // Server -> Client
    GQL_CONNECTION_ERROR("connection_error"), // Server -> Client


    GQL_START("start"), // Client -> Server
    GQL_DATA("data"),// Server -> Client
    GQL_ERROR("error"), // Server -> Client

    ;

    public final String type;

    TypePacket(String type) {
        this.type = type;
    }

    public static TypePacket of(String sType) {
        for (TypePacket item : TypePacket.values()) {
            if (item.type.equals(sType)) {
                return item;
            }
        }
        return null;
    }
}
