package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public interface OnDeathListener extends CharListener
{
	public void onDeath(L2Character actor, L2Character killer);
}
