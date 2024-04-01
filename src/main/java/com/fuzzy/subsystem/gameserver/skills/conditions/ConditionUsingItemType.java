package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.Inventory;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class ConditionUsingItemType extends Condition
{
	private final long _mask;

	public ConditionUsingItemType(long mask)
	{
		_mask = mask;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(env.character.isPlayer())
		{
			Inventory inv = ((L2Player) env.character).getInventory();
			return (_mask & inv.getWearedMask()) != 0;
		}
		return env.character.getActiveWeaponItem() == null ? ((_mask & env.character.getFistWeaponType().mask()) != 0) : ((_mask & env.character.getActiveWeaponItem().getItemMask()) != 0);
	}
}
