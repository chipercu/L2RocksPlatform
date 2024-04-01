package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExNewSkillToLearnByLevelUp extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new ExNewSkillToLearnByLevelUp();

    @Override
    protected void writeImpl() {
        writeEx(0xFD);
    }
}
