package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.clan_find.*;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeRecruitBoardDetail;

public class RequestPledgeRecruitBoardDetail extends L2GameClientPacket {
    private int _clanId;

    @Override
    protected void readImpl() {
        _clanId = readD();
    }

    @Override
    protected void runImpl() {
        final L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        final PledgeRecruitInfo pledgeRecruitInfo = ClanEntryManager.getInstance().getClanById(_clanId);
        if (pledgeRecruitInfo == null)
            return;

        getClient().sendPacket(new ExPledgeRecruitBoardDetail(pledgeRecruitInfo));
    }
}