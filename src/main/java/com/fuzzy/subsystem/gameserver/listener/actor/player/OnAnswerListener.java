package com.fuzzy.subsystem.gameserver.listener.actor.player;

import com.fuzzy.subsystem.gameserver.listener.PlayerListener;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * @author VISTALL
 * @date 9:37/15.04.2011
 */
public interface OnAnswerListener extends PlayerListener
{
	void sayYes(L2Player L2Player);

	void sayNo(L2Player L2Player);
}
