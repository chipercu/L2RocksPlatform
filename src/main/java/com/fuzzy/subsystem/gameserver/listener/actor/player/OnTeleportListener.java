package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnTeleportListener extends PlayerListener
{
	public void onTeleport(L2Player player, int x, int y, int z, int reflection);
}
