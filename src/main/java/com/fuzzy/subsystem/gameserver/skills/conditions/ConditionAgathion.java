package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionAgathion extends Condition
{
	private final int _agathionId;

	public ConditionAgathion(int agathionId)
	{
		_agathionId = agathionId;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer() || ((L2Player) env.character).getAgathion() == null)
			return false;
		return ((L2Player) env.character).getAgathion().getId() == _agathionId;
	}
}