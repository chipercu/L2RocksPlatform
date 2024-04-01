package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.siege.territory.TerritorySiege;
import com.fuzzy.subsystem.gameserver.serverpackets.ExReplyDominionInfo;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowOwnthingPos;

public class RequestExDominionInfo extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		activeChar.sendPacket(new ExReplyDominionInfo());
		if(TerritorySiege.isInProgress())
			activeChar.sendPacket(new ExShowOwnthingPos());
	}

	@Override
	public void readImpl()
	{}
}