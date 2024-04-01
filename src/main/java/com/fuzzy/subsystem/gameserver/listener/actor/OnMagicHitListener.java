package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;

public interface OnMagicHitListener extends CharListener
{
	public void onMagicHit(L2Character actor, L2Skill skill, L2Character caster);
}
