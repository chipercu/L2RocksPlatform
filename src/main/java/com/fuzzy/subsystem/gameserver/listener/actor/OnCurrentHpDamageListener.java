package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;

public interface OnCurrentHpDamageListener extends CharListener
{
	public void onCurrentHpDamage(L2Character actor, double damage, L2Character attacker, L2Skill skill, boolean crit);
}
