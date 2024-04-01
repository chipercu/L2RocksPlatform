package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Skill.SkillType;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class ConditionUsingSkill extends Condition
{
	private final int _skillId;
	private final SkillType _skillType;

	public ConditionUsingSkill(int skillId)
	{
		_skillId = skillId;
		_skillType = null;
	}

	public ConditionUsingSkill(String skilltype)
	{
		_skillId = -1;
		_skillType = SkillType.valueOf(skilltype);
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.skill == null)
			return false;
		return env.skill.getId() == _skillId || env.skill.getSkillType() == _skillType;
	}
}