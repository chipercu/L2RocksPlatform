package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class RDam extends L2Skill
{
	public RDam(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && !target.isDead())
			{
				activeChar.sendHDmgMsg(activeChar, target, this, (int)getPower(), false, false);
				target.reduceCurrentHp(getPower(), activeChar, this, true, true, true, true, false, getPower(), true, false, false, false);
				getEffects(activeChar, target, false, false);
			}
	}
}