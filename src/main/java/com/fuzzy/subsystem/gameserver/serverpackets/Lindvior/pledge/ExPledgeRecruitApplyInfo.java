package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge;

import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryStatus;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExPledgeRecruitApplyInfo extends L2GameServerPacket {
    private final ClanEntryStatus _status;

    public ExPledgeRecruitApplyInfo(ClanEntryStatus status) {
        _status = status;
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x14A);

        writeD(_status.ordinal());
    }
}