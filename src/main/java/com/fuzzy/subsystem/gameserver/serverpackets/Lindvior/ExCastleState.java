package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.model.entity.residence.Castle;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExCastleState extends L2GameServerPacket {
    private final int _id;

    public ExCastleState(Castle castle) {
        _id = castle.getId();
    }

    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0x133);
        writeD(_id);
        writeD(0x00);
    }
}