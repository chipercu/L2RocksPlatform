package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryManager;
import com.fuzzy.subsystem.gameserver.serverpackets.*;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeDraftListSearch;

public class RequestPledgeDraftListSearch extends L2GameClientPacket {
    private int _levelMin;
    private int _levelMax;
    private int _classId;
    private String _query;
    private int _sortBy;
    private boolean _descending;

    @Override
    protected void readImpl() {
        _levelMin = ClanEntryManager.constrain(readD(), 0, 107);
        _levelMax = ClanEntryManager.constrain(readD(), 0, 107);
        _classId = readD();
        _query = readS();
        _sortBy = readD();
        _descending = readD() == 2;
    }

    @Override
    protected void runImpl() {
        final L2Player activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        if (_query.isEmpty())
            sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().getSortedWaitingList(_levelMin, _levelMax, _classId, _sortBy, _descending)));
        else
            sendPacket(new ExPledgeDraftListSearch(ClanEntryManager.getInstance().queryWaitingListByName(_query.toLowerCase())));
    }
}