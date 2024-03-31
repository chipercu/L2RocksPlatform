package com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.packet;

import com.fuzzy.network.packet.IPacket;
import com.fuzzy.platform.component.frontend.engine.network.protocol.graphqlws.packet.TypePacket;
import net.minidev.json.JSONObject;

public class Packet implements IPacket {

    public final String id;
    public final TypePacket type;
    public final JSONObject payload;

    public Packet(TypePacket type) {
        this(null, type, null);
    }

    public Packet(String id, TypePacket type) {
        this(id, type, null);
    }

    public Packet(String id, TypePacket type, JSONObject payload) {
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public String serialize() {
        JSONObject out = new JSONObject();
        if (id != null) {
            out.put("id", id);
        }
        out.put("type", type.type);
        if (payload != null) {
            out.put("payload", payload);
        }
        return out.toJSONString();
    }

    public static Packet parse(JSONObject parse) {
        String id = parse.getAsString("id");
        TypePacket type = TypePacket.of(parse.getAsString("type"));
        JSONObject payload = (JSONObject) parse.get("payload");
        return new Packet(id, type, payload);
    }
}
