package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class MDam extends L2Skill
{
    public MDam(StatsSet set)
	{
        super(set);
    }

    @Override
    public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
        int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : (activeChar.getChargedSoulShot() ? 2 : 0)) : 0;

        for(L2Character target : targets)
            if(target != null)
			{
                if(target.isDead() || target.isInvul() || target.block_hp.get())
                    continue;

                if(getPower() == 0 && target.checkReflectSkill(activeChar, this))
					target = activeChar;

                double damage = Formulas.calcMagicDam(activeChar, target, this, sps, false);

				if(getId() == 1400)
				{
					Formulas.calcLethalHit(activeChar, target, this);
					if(damage >= 1)
						target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
				}
				else
				{
					if (damage >= 1)
						target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, damage, true, false, false, false);
					Formulas.calcLethalHit(activeChar, target, this);
				}

                getEffects(activeChar, target, getActivateRate() > 0, false);
            }

		if(isSuicideAttack())
		{
			activeChar.doDie(null);
			activeChar.onDecay();
		}
		else if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}