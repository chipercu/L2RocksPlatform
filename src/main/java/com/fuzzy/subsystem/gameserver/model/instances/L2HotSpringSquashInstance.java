package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.entity.siege.clanhall.RainbowSpringSiege;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2HotSpringSquashInstance extends L2MonsterInstance
{
	public L2HotSpringSquashInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void doDie(L2Character killer)
	{
		RainbowSpringSiege.getInstance().onDieSquash(this);
		super.doDie(killer);
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}
