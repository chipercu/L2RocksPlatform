package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExBlockAddResult extends L2GameServerPacket {
    private final String _blockName;

    public ExBlockAddResult(String name) {
        _blockName = name;
    }

    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0xED);
        writeD(0x01);
        writeS(_blockName);
    }
}
