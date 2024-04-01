package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnSetLevelListener extends PlayerListener
{
	public void onSetLevel(L2Player player, int level);
}
