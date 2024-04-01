package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Location;

public class CannotMoveAnymore extends L2GameClientPacket
{
	private Location _loc = new Location();

	/**
	 * packet type id 0x47
	 *
	 * sample
	 *
	 * 36
	 * a8 4f 02 00 // x
	 * 17 85 01 00 // y
	 * a7 00 00 00 // z
	 * 98 90 00 00 // heading?
	 *
	 * format:		cdddd
	 * @param decrypt
	 */
	@Override
	public void readImpl()
	{
		_loc.x = readD();
		_loc.y = readD();
		_loc.z = readD();
		_loc.h = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		if(!activeChar.p_block_controll.get())
			activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, _loc, null);
	}
}