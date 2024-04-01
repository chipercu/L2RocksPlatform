package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnSetPrivateStoreType extends PlayerListener
{
	public void onSetPrivateStoreType(L2Player player, short type);
}
