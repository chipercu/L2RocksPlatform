package com.fuzzy.subsystem.common.loginservercon.gspackets;

import com.fuzzy.subsystem.common.loginservercon.Attribute;
import com.fuzzy.subsystem.util.GArray;

public class ServerStatus extends GameServerBasePacket {

    public ServerStatus(GArray<Attribute> attributes) {
        writeC(0x06);
        writeD(attributes.size());
        for (Attribute temp : attributes) {
            writeD(temp.id);
            writeD(temp.value);
        }

        attributes.clear();
    }
}