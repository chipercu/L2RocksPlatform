package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

/**
 * @author Smo
 */
public class ExBR_NewIConCashBtnWnd extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        writeC(EXTENDED_PACKET);
        writeH(0x144);
        writeH(0);
    }
}
