package com.fuzzy.subsystem.gameserver.skills.conditions;

import gnu.trove.TIntHashSet;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionTargetForbiddenClassId extends Condition
{
	private TIntHashSet _classIds = new TIntHashSet();

	public ConditionTargetForbiddenClassId(String[] ids)
	{
		for(String id : ids)
			_classIds.add(Integer.parseInt(id));
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character target = env.target;
		if(!target.isPlayable())
			return false;
		return !target.isPlayer() || !_classIds.contains(target.getPlayer().getActiveClassId());
	}
}