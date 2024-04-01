package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExAgitAuctionCmdPacket extends L2GameServerPacket {
    // 0xfe:0xd1 ExAgitAuctionCmdPacket

    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0xD2);
    }
}
