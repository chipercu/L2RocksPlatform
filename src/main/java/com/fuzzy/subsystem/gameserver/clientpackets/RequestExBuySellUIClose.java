package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ItemList;

public class RequestExBuySellUIClose extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
	// trigger
	}

	@Override
	public void readImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.isInventoryDisabled())
			return;

		activeChar.setBuyListId(0);
		activeChar.sendPacket(new ItemList(activeChar, true));
	}
}