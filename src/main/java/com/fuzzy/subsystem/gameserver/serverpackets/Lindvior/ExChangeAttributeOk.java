package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExChangeAttributeOk extends L2GameServerPacket {

    public static final L2GameServerPacket STATIC = new ExChangeAttributeOk();

    public ExChangeAttributeOk() {
        //
    }

    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0x11a);
    }
}