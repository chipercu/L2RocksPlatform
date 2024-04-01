package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2WorldRegion;

public class RequestReload extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{}

	@Override
	public void runImpl()
	{
		L2Player player = getClient().getActiveChar();
		if(player == null)
			return;
		if(System.currentTimeMillis() - player.getLastRequestReloadPacket() < ConfigValue.RequestReloadPacketDelay)
		{
			player.sendActionFailed();
			return;
		}
		player.setLastRequestReloadPacket();
		player.sendUserInfo(true);

		if(player.getCurrentRegion() != null)
			for(L2WorldRegion neighbor : player.getCurrentRegion().getNeighbors())
				neighbor.showObjectsToPlayer(player, false);
	}
}