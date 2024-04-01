package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionPlayerMinHp extends Condition
{
	private final float _hp;

	public ConditionPlayerMinHp(int hp)
	{
		_hp = hp;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.character.getCurrentHp() > _hp;
	}
}