package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.instances.L2SiegeHeadquarterInstance;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

/**
 * @author : Ragnarok
 * @date : 29.08.11   17:25
 */
public class CubHeal extends L2Skill {
    private int cubHealPower;

    @Override
    public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first) 
	{
        return !(target == null || target.isDoor() || target instanceof L2SiegeHeadquarterInstance) && super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    public CubHeal(StatsSet set) 
	{
        super(set);
        cubHealPower = set.getInteger("cubHealPower", 0);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets) 
	{
        for (L2Character target : targets) 
		{
            if (target != null && !target.isDead() && !target.isHealBlocked(true, false) && !target.block_hp.get()) 
			{
                double maxNewHp = cubHealPower * target.calcStat(Stats.HEAL_EFFECTIVNESS, 100, activeChar, this) / 100;
				maxNewHp = activeChar.calcStat(Stats.HEAL_POWER, maxNewHp, target, this);
                double addToHp = Math.max(0, maxNewHp);
                if(addToHp > 0) 
                    target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                target.sendPacket(Msg.REJUVENATING_HP);
                getEffects(activeChar, target, getActivateRate() > 0, false);
            }
        }
    }
}
