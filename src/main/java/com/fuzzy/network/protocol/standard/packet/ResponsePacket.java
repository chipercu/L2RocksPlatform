package com.fuzzy.network.protocol.standard.packet;

import com.fuzzy.network.packet.IPacket;
import com.fuzzy.network.protocol.standard.packet.IPacketId;
import com.fuzzy.network.protocol.standard.packet.Packet;
import com.fuzzy.network.protocol.standard.packet.TypePacket;
import net.minidev.json.JSONObject;

/**
 * Created by kris on 26.08.16.
 */
public class ResponsePacket extends Packet implements com.fuzzy.network.protocol.standard.packet.IPacketId {

    private final long id;
    private final int code;

    public ResponsePacket(long id, int code, JSONObject data) {
        super(data);
        this.id = id;
        this.code = code;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public com.fuzzy.network.protocol.standard.packet.TypePacket getType() {
        return TypePacket.RESPONSE;
    }

    public int getCode() {
        return code;
    }

    @Override
    protected void serializeNative(JSONObject jsonObject) {
        jsonObject.put("id", id);
        jsonObject.put("code", code);
    }

    public static ResponsePacket build(com.fuzzy.network.protocol.standard.packet.IPacketId request, int code, JSONObject data) {
        return new ResponsePacket(request.getId(), code, data);
    }

    public static IPacket[] response(IPacketId request, int code, JSONObject data) {
        return new IPacket[]{ build(request, code, data) };
    }
}
