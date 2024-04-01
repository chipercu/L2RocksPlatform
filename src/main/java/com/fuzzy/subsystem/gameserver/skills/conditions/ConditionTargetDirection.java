package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.util.Util;

public class ConditionTargetDirection extends Condition
{
	private final Util.TargetDirection _dir;

	public ConditionTargetDirection(Util.TargetDirection direction)
	{
		_dir = direction;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return Util.getDirectionTo(env.target, env.character) == _dir;
	}
}
