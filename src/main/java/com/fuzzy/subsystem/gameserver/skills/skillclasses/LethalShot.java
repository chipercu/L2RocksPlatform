package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.Formulas.AttackInfo;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class LethalShot extends L2Skill
{
	public LethalShot(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if(ss)
			activeChar.unChargeShots(false);

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				boolean reflected = target.checkReflectSkill(activeChar, this);
				if(reflected)
					target = activeChar;

				if(getPower() >= 0) // Если меньше 0 значит скилл "отключен"
				{
					AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss, false, false, false);
					target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, info.damage, true, false, info.crit, false);
					if(!reflected)
						target.doCounterAttack(this, activeChar);
					Formulas.calcLethalHit(activeChar, target, this);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
	}
}
