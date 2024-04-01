package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

/**
 * @author Smo
 */
public class ExPledgeWaitingListAlarm extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        writeEx(0x151);
    }
}
