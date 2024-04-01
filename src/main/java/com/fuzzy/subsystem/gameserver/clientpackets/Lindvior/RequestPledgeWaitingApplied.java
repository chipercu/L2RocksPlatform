package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryManager;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeWaitingListApplied;

public class RequestPledgeWaitingApplied extends L2GameClientPacket {
    @Override
    protected void readImpl() {

    }

    @Override
    protected void runImpl() {
        L2Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.getClan() != null)
            return;

        int clanId = ClanEntryManager.getInstance().getClanIdForPlayerApplication(activeChar.getObjectId());
        if (clanId > 0)
            activeChar.sendPacket(new ExPledgeWaitingListApplied(clanId, activeChar.getObjectId()));
    }
}