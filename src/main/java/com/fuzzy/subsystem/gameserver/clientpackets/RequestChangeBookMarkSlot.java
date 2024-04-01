package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestChangeBookMarkSlot extends L2GameClientPacket
{
	private int slot_old, slot_new;

	@Override
	public void readImpl()
	{
		slot_old = readD();
		slot_new = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		_log.info(getType() + "@" + activeChar + "::" + slot_old + "::" + slot_new);
	}
}