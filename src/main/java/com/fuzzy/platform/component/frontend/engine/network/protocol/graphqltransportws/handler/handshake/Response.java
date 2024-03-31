package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake;

import com.fuzzy.network.struct.HandshakeData;
import net.minidev.json.JSONObject;

public record Response(HandshakeData handshakeData, JSONObject payload) {
}
