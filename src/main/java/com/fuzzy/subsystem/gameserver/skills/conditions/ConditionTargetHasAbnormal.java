package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.SkillAbnormalType;

public final class ConditionTargetHasAbnormal extends Condition
{
	private final SkillAbnormalType _abnormal;
	private final int _level;

	public ConditionTargetHasAbnormal(SkillAbnormalType abnormal, int level)
	{
		_abnormal = abnormal;
		_level = level;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character target = env.target;
		if(target == null)
			return false;
		L2Effect effect = target.getEffectList().getEffectByStackType(_abnormal);
		if(effect == null)
			return false;
		else if(_level == -1 || effect.getSkill().getLevel() >= _level)
			return true;
		return false;
	}
}
