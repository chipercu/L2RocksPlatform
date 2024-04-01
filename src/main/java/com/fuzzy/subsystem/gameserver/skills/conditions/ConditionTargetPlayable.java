package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Playable;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionTargetPlayable extends Condition
{
	private final boolean _flag;

	public ConditionTargetPlayable(boolean flag)
	{
		_flag = flag;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		//return env.target.isPlayable() == _flag;
		return env.target instanceof L2Playable == _flag;
	}
}
