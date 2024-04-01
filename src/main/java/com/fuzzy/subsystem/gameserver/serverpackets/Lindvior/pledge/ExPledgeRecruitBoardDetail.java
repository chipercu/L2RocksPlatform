package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge;

import com.fuzzy.subsystem.gameserver.model.clan_find.*;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExPledgeRecruitBoardDetail extends L2GameServerPacket {
    final PledgeRecruitInfo _pledgeRecruitInfo;

    public ExPledgeRecruitBoardDetail(PledgeRecruitInfo pledgeRecruitInfo) {
        _pledgeRecruitInfo = pledgeRecruitInfo;
    }

    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x14C);

        writeD(_pledgeRecruitInfo.getClanId());
        writeD(_pledgeRecruitInfo.getKarma());
        writeS(_pledgeRecruitInfo.getInformation());
        writeS(_pledgeRecruitInfo.getDetailedInformation());
        //writeD(_pledgeRecruitInfo.getApplicationType());
        //writeD(_pledgeRecruitInfo.getRecruitType());
    }
}