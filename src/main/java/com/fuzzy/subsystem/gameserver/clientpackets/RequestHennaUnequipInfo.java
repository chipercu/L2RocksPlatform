package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.HennaUnequipInfo;
import com.fuzzy.subsystem.gameserver.tables.HennaTable;
import com.fuzzy.subsystem.gameserver.templates.L2Henna;

public class RequestHennaUnequipInfo extends L2GameClientPacket
{
	private int _symbolId;

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if (player == null)
			return;

		L2Henna henna = HennaTable.getInstance().getTemplate(_symbolId);
		if (henna != null)
			player.sendPacket(new HennaUnequipInfo(henna, player));
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		_symbolId = readD();
	}
}