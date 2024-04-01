package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge;

import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryManager;
import com.fuzzy.subsystem.gameserver.model.clan_find.PledgeApplicantInfo;
import com.fuzzy.subsystem.gameserver.model.clan_find.PledgeRecruitInfo;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

/**
 * @author Sdw
 */
public class ExPledgeWaitingListApplied extends L2GameServerPacket {
    private final PledgeApplicantInfo _pledgePlayerRecruitInfo;
    private final PledgeRecruitInfo _pledgeRecruitInfo;

    public ExPledgeWaitingListApplied(int clanId, int playerId) {
        _pledgePlayerRecruitInfo = ClanEntryManager.getInstance().getPlayerApplication(clanId, playerId);
        _pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(clanId);
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x14D);

        writeD(_pledgeRecruitInfo.getClan().getClanId());
        writeS(_pledgeRecruitInfo.getClan().getName());
        writeS(_pledgeRecruitInfo.getClan().getLeaderName());
        writeD(_pledgeRecruitInfo.getClan().getLevel());
        writeD(_pledgeRecruitInfo.getClan().getMembersCount());
        writeD(_pledgeRecruitInfo.getKarma());
        writeS(_pledgeRecruitInfo.getInformation());
        writeS(_pledgePlayerRecruitInfo.getMessage());
    }
}