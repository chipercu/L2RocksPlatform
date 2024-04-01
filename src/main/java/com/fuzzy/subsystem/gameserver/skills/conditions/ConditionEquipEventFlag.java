package com.fuzzy.subsystem.gameserver.skills.conditions;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class ConditionEquipEventFlag extends Condition
{
	public ConditionEquipEventFlag()
	{}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;
		L2ItemInstance flag = ((L2Player) env.character).getActiveWeaponInstance();
		//_log.info("ConditionEquipEventFlag: "+flag+" "+(flag == null ? "" : flag.getCustomType1()));
		return flag == null || flag.getCustomType1() != 77; // 77 это эвентовый флаг
	}
}
