package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.instances.L2HennaInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.HennaItemInfo;
import com.fuzzy.subsystem.gameserver.tables.HennaTable;
import com.fuzzy.subsystem.gameserver.templates.L2Henna;

public class RequestHennaItemInfo extends L2GameClientPacket
{
	// format  cd
	private int SymbolId;

	@Override
	public void readImpl()
	{
		SymbolId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2Henna template = HennaTable.getInstance().getTemplate(SymbolId);
		if(template != null)
			activeChar.sendPacket(new HennaItemInfo(new L2HennaInstance(template), activeChar));
	}
}