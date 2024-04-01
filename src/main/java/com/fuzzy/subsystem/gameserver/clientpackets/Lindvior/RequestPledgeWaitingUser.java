package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.clan_find.*;
import com.fuzzy.subsystem.gameserver.serverpackets.*;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeWaitingList;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeWaitingUser;

public class RequestPledgeWaitingUser extends L2GameClientPacket {
    private int _clanId;
    private int _playerId;

    @Override
    protected void readImpl() {
        _clanId = readD();
        _playerId = readD();
    }

    @Override
    protected void runImpl() {
        final L2Player activeChar = getClient().getActiveChar();
        if ((activeChar == null) || (activeChar.getClanId() != _clanId)) {
            return;
        }

        final PledgeApplicantInfo infos = ClanEntryManager.getInstance().getPlayerApplication(_clanId, _playerId);
        if (infos == null) {
            sendPacket(new ExPledgeWaitingList(_clanId));
        } else {
            sendPacket(new ExPledgeWaitingUser(infos));
        }
    }
}