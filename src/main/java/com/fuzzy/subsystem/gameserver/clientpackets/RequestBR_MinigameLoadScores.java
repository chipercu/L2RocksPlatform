package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestBR_MinigameLoadScores extends L2GameClientPacket
{
	@Override
	protected void readImpl() throws Exception
	{
	//just a trigger
	}

	@Override
	protected void runImpl() throws Exception
	{
		L2Player player = getClient().getActiveChar();
		//if(player == null || !Config.EX_JAPAN_MINIGAME)
		//	return;
		//player.sendPacket(new ExBR_MinigameLoadScoresPacket(player));
	}
}