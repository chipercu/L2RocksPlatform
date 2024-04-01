package com.fuzzy.subsystem.gameserver.listener.actor;

import com.fuzzy.subsystem.gameserver.listener.CharListener;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;

public interface OnMagicUseListener extends CharListener
{
	public void onMagicUse(L2Character actor, L2Skill skill, L2Character target, boolean alt);
}
