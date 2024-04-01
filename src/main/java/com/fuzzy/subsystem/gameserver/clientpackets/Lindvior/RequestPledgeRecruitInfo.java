package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Clan;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeRecruitInfo;
import com.fuzzy.subsystem.gameserver.tables.ClanTable;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitInfo extends L2GameClientPacket {
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

        final L2Clan clan = ClanTable.getInstance().getClan(_clanId);
        if (clan == null)
            return;

        getClient().sendPacket(new ExPledgeRecruitInfo(_clanId));
    }
}