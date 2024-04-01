package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExTeleportToLocationActivate extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        writeEx(0x154);
    }
}
