package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnPlayerPartyLeaveListener extends PlayerListener
{
	public void onPartyLeave(L2Player L2Player);
}
