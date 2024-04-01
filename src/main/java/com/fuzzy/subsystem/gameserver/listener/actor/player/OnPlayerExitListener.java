package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnPlayerExitListener extends PlayerListener
{
	public void onPlayerExit(L2Player L2Player);
}
