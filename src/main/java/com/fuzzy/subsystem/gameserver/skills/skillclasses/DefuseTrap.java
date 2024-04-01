package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.instances.L2TrapInstance;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class DefuseTrap extends L2Skill
{
	public DefuseTrap(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null || !target.isTrap())
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null && target.isTrap())
			{

				L2TrapInstance trap = (L2TrapInstance) target;
				if(trap.getLevel() <= getPower())
					trap.destroy();
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}