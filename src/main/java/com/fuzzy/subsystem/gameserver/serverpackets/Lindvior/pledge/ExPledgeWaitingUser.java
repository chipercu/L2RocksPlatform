package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge;

import com.fuzzy.subsystem.gameserver.model.clan_find.PledgeApplicantInfo;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

public class ExPledgeWaitingUser extends L2GameServerPacket {
    private final PledgeApplicantInfo _pledgeRecruitInfo;

    public ExPledgeWaitingUser(PledgeApplicantInfo pledgeRecruitInfo) {
        _pledgeRecruitInfo = pledgeRecruitInfo;
    }

    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x14F);

        writeD(_pledgeRecruitInfo.getPlayerId());
        writeS(_pledgeRecruitInfo.getMessage());
    }
}