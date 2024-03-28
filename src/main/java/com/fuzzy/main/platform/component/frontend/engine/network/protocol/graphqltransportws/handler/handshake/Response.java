package com.fuzzy.main.platform.component.frontend.engine.network.protocol.graphqltransportws.handler.handshake;

import com.fuzzy.main.network.struct.HandshakeData;
import net.minidev.json.JSONObject;

public record Response(HandshakeData handshakeData, JSONObject payload) {
}
