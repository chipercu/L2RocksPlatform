package com.fuzzy.subsystem.gameserver.clientpackets.Lindvior;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.clan_find.ClanEntryManager;
import com.fuzzy.subsystem.gameserver.serverpackets.Lindvior.pledge.ExPledgeRecruitBoardSearch;

public class RequestPledgeRecruitBoardSearch extends L2GameClientPacket {
    private int _clanLevel;
    private int _karma;
    private int _type;
    private String _query;
    private int _sort;
    private boolean _descending;
    private int _page;

    @Override
    protected void readImpl() {
        _clanLevel = readD();
        _karma = readD();
        _type = readD();
        _query = readS();
        _sort = readD();
        _descending = readD() == 2;
        _page = readD();
    }

    @Override
    protected void runImpl() {
        final L2Player activeChar = getClient().getActiveChar();

        if (activeChar == null)
            return;

        if (_query.isEmpty()) {
            if (_karma < 0 && _clanLevel < 0)
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getUnSortedClanList(), _page));
            else
                activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanList(_clanLevel, _karma, _sort, _descending), _page));
        } else
            activeChar.sendPacket(new ExPledgeRecruitBoardSearch(ClanEntryManager.getInstance().getSortedClanListByName(_query.toLowerCase(), _type), _page));
    }
}