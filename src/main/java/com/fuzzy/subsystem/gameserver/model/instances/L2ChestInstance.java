package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Rnd;
import com.fuzzy.subsystem.util.Util;

public class L2ChestInstance extends L2MonsterInstance
{
	public L2ChestInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void tryOpen(L2Player opener, L2Skill skill)
	{
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
	
	@Override
	public void doDie(L2Character killer)
	{
		if(getTemplate().getNpcId() == 35593)
		{
			int[] item = { 8035, 8037, 8039, 8040, 8046, 8047, 8050, 8051, 8052, 8053, 8054 };

			long count = Util.rollDrop(1, 1, 150000, false, killer.getPlayer());
			if(killer.isPet() || killer.isSummon())
				killer = killer.getPlayer();
			dropItem((L2Player) killer, item[Rnd.get(10)], count);
			RainbowSpringSiege.getInstance().chestDie(killer, this);
		}
		super.doDie(killer);
	}
}