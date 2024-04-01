package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.L2Skill.AddedSkill;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.util.GArray;

public class EffectCallSkills extends L2Effect
{
	public EffectCallSkills(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		for(AddedSkill as : getSkill().getAddedSkills())
		{
			GArray<L2Character> targets = new GArray<L2Character>();
			targets.add(getEffected());
			getEffector().callSkill(as.getSkill(), targets, false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}