package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.HennaEquipList;
import com.fuzzy.subsystem.gameserver.tables.HennaTreeTable;

public class RequestHennaList extends L2GameClientPacket
{
	// format: cd
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	public void readImpl()
	{
		_unknown = readD(); // ??
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.sendPacket(new HennaEquipList(activeChar, HennaTreeTable.getInstance().getAvailableHenna(activeChar.getClassId(), activeChar.getSex())));
	}
}