package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.instances.*;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;

public class Pokemon extends L2Skill
{
	public Pokemon(StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(L2Character activeChar, L2Character target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(target == null)
			return false;
		if(!(target instanceof PokemonGoNpcInstance))
		{
			activeChar.sendPacket(Msg.INVALID_TARGET());
			return false;
		}
		PokemonGoNpcInstance npc = (PokemonGoNpcInstance) target;
		if(!npc.can_cast(activeChar.getPlayer()))
			return false;
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(L2Character activeChar, GArray<L2Character> targets)
	{
		if(!activeChar.isPlayer())
			return;

		L2Player player = (L2Player) activeChar;

		for(L2Character targ : targets)
		{
			if(targ == null || !(targ instanceof PokemonGoNpcInstance))
				continue;

			PokemonGoNpcInstance npc = (PokemonGoNpcInstance) targ;
			npc.give_drop(activeChar.getPlayer());
		}
	}
}