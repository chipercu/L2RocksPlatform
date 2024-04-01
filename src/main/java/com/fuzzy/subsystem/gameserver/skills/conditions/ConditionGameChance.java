package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.util.Rnd;

public class ConditionGameChance extends Condition
{
	private final int _chance;

	ConditionGameChance(int chance)
	{
		_chance = chance;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return Rnd.chance(_chance);
	}
}
