package com.fuzzy.subsystem.gameserver.skills.funcs;

import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;

public class FuncSet extends Func
{
	public FuncSet(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner, value);
	}

	@Override
	public void calc(Env env)
	{
		env.value = _value;
	}
}
