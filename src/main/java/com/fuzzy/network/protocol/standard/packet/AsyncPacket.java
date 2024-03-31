package com.fuzzy.network.protocol.standard.packet;

import com.fuzzy.network.protocol.standard.packet.TargetPacket;
import com.fuzzy.network.protocol.standard.packet.TypePacket;
import net.minidev.json.JSONObject;

/**
 * Created by kris on 26.08.16.
 */
public class AsyncPacket extends TargetPacket {

    public AsyncPacket(String controller, String action, JSONObject data) {
        super(controller, action, data);
    }

    @Override
    public com.fuzzy.network.protocol.standard.packet.TypePacket getType() {
        return TypePacket.ASYNC;
    }

}
