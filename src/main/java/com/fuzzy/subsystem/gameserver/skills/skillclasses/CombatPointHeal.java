package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class CombatPointHeal extends L2Skill
{
	public CombatPointHeal(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead() || target.isHealBlocked(true, false) || target.block_hp.get())
					continue;

				double maxNewCp = _power * target.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100, activeChar, this) / 100;
				double addToCp = Math.max(0, maxNewCp);
				if(addToCp > 0)
					addToCp = target.setCurrentCp(addToCp + target.getCurrentCp());
				target.sendPacket(new SystemMessage(SystemMessage.S1_CPS_WILL_BE_RESTORED).addNumber((long) addToCp));
				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
