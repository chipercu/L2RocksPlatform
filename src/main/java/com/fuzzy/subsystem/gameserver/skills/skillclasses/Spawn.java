package com.fuzzy.subsystem.gameserver.skills.skillclasses;

import com.fuzzy.subsystem.common.ThreadPoolManager;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2ObjectTasks;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.model.L2Spawn;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.tables.NpcTable;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;
import com.fuzzy.subsystem.gameserver.templates.StatsSet;
import com.fuzzy.subsystem.util.GArray;
import com.fuzzy.subsystem.util.Rnd;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Spawn extends L2Skill
{
	private static final Logger _log = Logger.getLogger(Spawn.class.getName());
	
	private final int _npcId;
	private final int _despawnDelay;
	private final boolean _summonSpawn;
	private final boolean _randomOffset;
	
	public Spawn(StatsSet set)
	{
		super(set);
		_npcId = set.getInteger("npcId", 0);
		_despawnDelay = set.getInteger("despawnDelay", 0);
		_summonSpawn = set.getBool("isSummonSpawn", false);
		_randomOffset = set.getBool("randomOffset", true);
	}
	
	@Override
	public void useSkill(L2Character caster, GArray<L2Character> targets)
	{
		if (caster.isAlikeDead())
		{
			return;
		}
		
		if (_npcId == 0)
		{
			_log.warning("NPC ID not defined for skill ID:" + getId());
			return;
		}
		
		final L2NpcTemplate template = NpcTable.getInstance().getTemplate(_npcId);
		if (template == null)
		{
			_log.warning("Spawn of the nonexisting NPC ID:" + _npcId + ", skill ID:" + getId());
			return;
		}
		
		L2Spawn spawn;
		try
		{
			spawn = new L2Spawn(template);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception in L2SkillSpawn: " + e.getMessage(), e);
			return;
		}
		
		int x = caster.getX();
		int y = caster.getY();
		if (_randomOffset)
		{
			x += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
			y += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
		}
		
		spawn.setLocx(x);
		spawn.setLocy(y);
		spawn.setLocz(caster.getZ());
		spawn.setHeading(caster.getHeading());
		spawn.stopRespawn();
		
		final L2NpcInstance npc = spawn.doSpawn(_summonSpawn);
		npc.setTitle(caster.getName());
		npc.setSummoner(caster);
		npc.updateAbnormalEffect();
		if (_despawnDelay > 0)
		{
			ThreadPoolManager.getInstance().schedule(new L2ObjectTasks.DeleteTask(npc), _despawnDelay);
		}
		npc.setWalking(); // Broadcast info
	}
}
