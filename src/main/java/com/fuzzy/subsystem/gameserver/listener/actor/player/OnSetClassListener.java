package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnSetClassListener extends PlayerListener
{
	public void onSetClass(L2Player actor, int class_id);
}
