package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class Balance extends L2Skill
{
	public Balance(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		double summaryCurrentHp = 0;
		int summaryMaximumHp = 0;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(false, true))
					continue;
				summaryCurrentHp += target.getCurrentHp();
				summaryMaximumHp += target.getMaxHp();
			}

		double percent = summaryCurrentHp / summaryMaximumHp;

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(false, true))
					continue;
				target.setCurrentHp(Math.max(0, target.getMaxHp() * percent), false);
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
