package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionPlayerMinMaxDamage extends Condition
{
	private final double _min;
	private final double _max;

	public ConditionPlayerMinMaxDamage(double min, double max)
	{
		_min = min;
		_max = max;
	}

	protected boolean testImpl(Env env)
	{
		if(_min > 0.0 && env.value < _min)
			return false;
		return (_max <= 0.0 || env.value <= _max);
	}
}
