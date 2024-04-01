package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnPlayerEnterListener extends PlayerListener
{
	public void onPlayerEnter(L2Player L2Player);
}
