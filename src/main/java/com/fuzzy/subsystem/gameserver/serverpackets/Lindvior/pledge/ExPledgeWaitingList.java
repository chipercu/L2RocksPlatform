package com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge;

import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryManager;
import com.fuzzy.subsystem.gameserver.model.clan_find.PledgeApplicantInfo;
import com.fuzzy.subsystem.gameserver.serverpackets.L2GameServerPacket;

import java.util.Map;

/**
 * @author Sdw
 */
public class ExPledgeWaitingList extends L2GameServerPacket {
    private final Map<Integer, PledgeApplicantInfo> pledgePlayerRecruitInfos;

    public ExPledgeWaitingList(int clanId) {
        pledgePlayerRecruitInfos = ClanEntryManager.getInstance().getApplicantListForClan(clanId);
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x14E);

        writeD(pledgePlayerRecruitInfos.size());
        for (PledgeApplicantInfo recruitInfo : pledgePlayerRecruitInfos.values()) {
            writeD(recruitInfo.getPlayerId());
            writeS(recruitInfo.getPlayerName());
            writeD(recruitInfo.getClassId());
            writeD(recruitInfo.getPlayerLvl());
        }
    }
}