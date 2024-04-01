package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.Hero;

/**
 * Format chS
 * c (id) 0xD0
 * h (subid) 0x05
 * S the hero's words :)
 */
public class RequestWriteHeroWords extends L2GameClientPacket
{
	private String _heroWords;

	protected void readImpl()
	{
		_heroWords = readS();
	}

	protected void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null || !player.isHero()) 
			return;
		if(_heroWords == null || _heroWords.length() > 300) 
			return;
		Hero.getInstance().setHeroMessage(player.getObjectId(), _heroWords);
	}
}