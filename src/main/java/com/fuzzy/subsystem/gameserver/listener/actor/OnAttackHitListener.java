package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;

public interface OnAttackHitListener extends CharListener
{
	public void onAttackHit(L2Character actor, L2Character attacker);
}
