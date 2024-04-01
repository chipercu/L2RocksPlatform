package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public interface OnPlayerSayListener extends PlayerListener
{
	void onSay(L2Player activeChar, int type, String target, String text);
}
