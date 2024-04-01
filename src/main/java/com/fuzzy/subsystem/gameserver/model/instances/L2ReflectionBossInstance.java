package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Party;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.L2World;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

import java.util.concurrent.ScheduledFuture;

public class L2ReflectionBossInstance extends L2RaidBossInstance
{
	private ScheduledFuture<?> _manaRegen;
	private final static int COLLAPSE_AFTER_DEATH_TIME = 5; // 5 мин

	public L2ReflectionBossInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		String refName = getReflection().getName();
		if(refName.contains("Kamaloka"))
			_manaRegen = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManaRegen(), 20000, 20000);
	}

	@Override
	public void doDie(L2Character killer)
	{
		String refName = getReflection().getName();
		if(refName.contains("Kamaloka"))
			if(ConfigValue.KamalokaLimit.equalsIgnoreCase("Leader"))
			{
				L2Party p = killer.getPlayer().getParty();
				if(p != null)
					p.getPartyLeader().setVarInst(refName, String.valueOf(System.currentTimeMillis()));
			}
			else
				for(L2Player p : L2World.getAroundPlayers(L2ReflectionBossInstance.this))
					if(p != null)
						p.setVarInst(refName, String.valueOf(System.currentTimeMillis()));

		if(_manaRegen != null)
		{
			_manaRegen.cancel(true);
			_manaRegen = null;
		}
		unspawnMinions();
		super.doDie(killer);
		clearReflection();
	}

	@Override
	public void unspawnMinions()
	{
		removeMinions();
	}

	/**
	 * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
	 */
	protected void clearReflection()
	{
		getReflection().clearReflection(COLLAPSE_AFTER_DEATH_TIME, true);
	}

	private class ManaRegen extends com.fuzzy.subsystem.common.RunnableImpl
	{
		public void runImpl()
		{
			try
			{
				for(L2Player p : L2World.getAroundPlayers(L2ReflectionBossInstance.this))
				{
					if(p == null || p.isDead() || p.isHealBlocked(true, false))
						continue;
					int addMp = getAddMp();
					if(addMp <= 0)
						return;
					double newMp = Math.min(Math.max(0, p.getMaxMp() - p.getCurrentMp()), addMp);
					if(newMp > 0)
						p.setCurrentMp(newMp + p.getCurrentMp());
					p.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(newMp)));
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		private int getAddMp()
		{
			switch(getLevel())
			{
				case 23:
				case 26:
					return 6;
				case 33:
				case 36:
					return 10;
				case 43:
				case 46:
					return 13;
				case 53:
				case 56:
					return 16; // С потолка
				case 63:
				case 66:
					return 19; // С потолка
				case 73:
					return 22; // С потолка
				default:
					return 0;
			}
		}
	}

	@Override
	public boolean isRaid()
	{
		return false;
	}

	@Override
	public boolean isRefRaid()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}
}