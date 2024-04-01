package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.Formulas.AttackInfo;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

public class ChargeSoul extends L2Skill
{
	private int _numSouls;

	public ChargeSoul(StatsSet set)
	{
		super(set);
		_numSouls = set.getInteger("numSouls", getLevel());
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
		if(ss && getTargetType() != SkillTargetType.TARGET_SELF)
			activeChar.unChargeShots(false);

		activeChar.setConsumedSouls(activeChar.getConsumedSouls() + _numSouls, null);

		for(L2Character target : targets)
			if(target != null)
			{
				if(target.isDead())
					continue;

				boolean reflected = target != activeChar && target.checkReflectSkill(activeChar, this);
				if(reflected)
					target = activeChar;

				if(getPower() > 0) // Если == 0 значит скилл "отключен"
				{
					if(Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, activeChar, this))) 
					{
						activeChar.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(activeChar));
						target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(activeChar));
						continue;
					}
					AttackInfo info = Formulas.calcPhysDam(activeChar, target, null, false, false, ss, false, true, false);
					target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, info.damage, true, false, info.crit, false);

					activeChar.sendHDmgMsg(target, activeChar, this, (int)info.damage, info.crit, false);
					if(!reflected)
						target.doCounterAttack(this, activeChar);
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}