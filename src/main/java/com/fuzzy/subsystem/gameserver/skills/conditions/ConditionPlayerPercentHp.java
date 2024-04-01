package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionPlayerPercentHp extends Condition
{
	private final float _hp;

	public ConditionPlayerPercentHp(int hp)
	{
		_hp = hp / 100f;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getCurrentHpRatio() <= _hp;
	}
}