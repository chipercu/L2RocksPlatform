package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeWaitingList;

public class RequestPledgeWaitingList extends L2GameClientPacket {
    private int _clanId;

    @Override
    protected void readImpl() {
        _clanId = readD();
    }

    @Override
    protected void runImpl() {
        final L2Player activeChar = getClient().getActiveChar();
        if ((activeChar == null) || (activeChar.getClanId() != _clanId)) {
            return;
        }

        sendPacket(new ExPledgeWaitingList(_clanId));
    }
}