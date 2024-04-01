package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.L2Character.HateInfo;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class ShiftAggression extends L2Skill
{
	public ShiftAggression(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(activeChar.getPlayer() == null)
			return;

		L2Playable playable = (L2Playable) activeChar;

		for(L2Character target : targets)
			if(target != null)
			{
				if(!target.isPlayer())
					continue;

				L2Player player_target = (L2Player) target;

				for(L2NpcInstance npc : L2World.getAroundNpc(activeChar, getAffectRange(), 200))
				{
					HateInfo hateInfo = playable.getHateList().get(npc);
					if(hateInfo == null || hateInfo.hate <= 0)
						continue;
					player_target.addDamageHate(npc, 0, hateInfo.hate + 100);
					hateInfo.hate = 0;
				}
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
	}
}
