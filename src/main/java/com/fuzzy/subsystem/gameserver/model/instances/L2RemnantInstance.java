package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.instancemanager.HellboundManager;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2RemnantInstance extends L2MonsterInstance
{
	private boolean _isBlessed;

	public L2RemnantInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public void doDie(L2Character killer)
	{
		if (HellboundManager.getInstance().getLevel() == 2 && isBlessed())
		{
			HellboundManager.getInstance().addPoints(5);
			decayMe();
		}

		super.doDie(killer);
	}

	public boolean isDead()
	{
		return false;
	}

	public boolean isBlessed()
	{
		return _isBlessed;
	}

	public void setBlessed(boolean blessed)
	{
		_isBlessed = blessed;
	}
}