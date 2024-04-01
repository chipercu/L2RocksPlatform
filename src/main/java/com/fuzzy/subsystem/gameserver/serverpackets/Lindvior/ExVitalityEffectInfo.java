package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExVitalityEffectInfo extends L2GameServerPacket {
    private final int points;
    private final int expBonus;

    public ExVitalityEffectInfo(L2Player player) {
        points = (int) player.getVitality() * 2;
        expBonus = (int) player.getVitalityBonus() * 100;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x11E);

        writeD(points);
        writeD(expBonus);
        writeD(0);// TODO: Remaining items count
        writeD(0);// TODO: Remaining items count
    }
}