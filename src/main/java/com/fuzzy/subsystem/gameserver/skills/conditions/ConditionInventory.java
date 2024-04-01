package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.skills.Env;

public abstract class ConditionInventory extends Condition implements ConditionListener
{
	protected final short _slot;

	public ConditionInventory(short slot)
	{
		_slot = slot;
	}

	@Override
	protected abstract boolean testImpl(Env env);
}