package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Summon;

/**
 * @author VISTALL
 * @date 15:37/05.08.2011
 */
public interface OnPlayerSummonServitorListener extends PlayerListener
{
	void onSummonServitor(L2Player L2Player, L2Summon servitor);
}
