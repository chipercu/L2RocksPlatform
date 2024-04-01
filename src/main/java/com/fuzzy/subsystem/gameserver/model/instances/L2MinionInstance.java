package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.util.Location;
import com.fuzzy.subsystem.util.Log;
import com.fuzzy.subsystem.util.PrintfFormat;

import java.lang.ref.WeakReference;

public class L2MinionInstance extends L2MonsterInstance
{
	private WeakReference<L2MonsterInstance> _master = null;

	public L2MinionInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	public L2MinionInstance(int objectId, L2NpcTemplate template, L2MonsterInstance leader)
	{
		super(objectId, template);
		_master = new WeakReference<L2MonsterInstance>(leader);
	}

	public L2MonsterInstance getLeader()
	{
		return _master != null ? _master.get() : null;
	}

	public void setLeader(L2MonsterInstance leader)
	{
		_master = new WeakReference<L2MonsterInstance>(leader);
	}

	public boolean isRaidFighter()
	{
		return getLeader() != null && getLeader().isRaid();
	}

	@Override
	public void doDie(L2Character killer)
	{
		if(getLeader() != null)
		{
			if(getLeader().isRaid())
				Log.add(PrintfFormat.LOG_BOSS_KILLED, new Object[] { getTypeName(),
						getName() + " {" + getLeader().getName() + "}", getNpcId(), killer, getX(), getY(), getZ(), "-" }, "bosses");
			getLeader().notifyMinionDied(this);
			getLeader().getAI().notifyEvent(CtrlEvent.EVT_PARTY_DEAD, killer, this);
		}
		super.doDie(killer);
	}

	@Override
	public boolean isFearImmune()
	{
		return isRaidFighter();
	}

	@Override
	public Location getSpawnedLoc()
	{
		return getLeader() != null ? getLeader().getMinionPosition() : getLoc();
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}

	@Override
	public int isUnDying()
	{
		return getTemplate().undying;
	}

	@Override
	public boolean isMinion()
	{
		return true;
	}
}