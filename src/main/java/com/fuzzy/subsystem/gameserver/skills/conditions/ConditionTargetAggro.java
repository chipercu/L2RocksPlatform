package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.instances.L2MonsterInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class ConditionTargetAggro extends Condition
{
	private final boolean _isAggro;

	public ConditionTargetAggro(boolean isAggro)
	{
		_isAggro = isAggro;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		L2Character target = env.target;
		if(target == null)
			return false;
		if(target.isMonster())
			return ((L2MonsterInstance) target).isAggressive() == _isAggro;
		if(target.isPlayer())
			return ((L2Player) target).getKarma() > 0;
		return false;
	}
}
