package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExJumpToLocation;

public class RequestExJump extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		sendPacket(new ExJumpToLocation(activeChar.getObjectId(), activeChar.getLoc(), activeChar.getLoc()));
		_log.info(getType());
	}

	@Override
	public void readImpl()
	{}
}