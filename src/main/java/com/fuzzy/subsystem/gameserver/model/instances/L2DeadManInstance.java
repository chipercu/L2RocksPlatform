package com.fuzzy.subsystem.gameserver.model.instances;

import com.fuzzy.subsystem.gameserver.ai.L2CharacterAI;
import com.fuzzy.subsystem.gameserver.model.L2Character;
import com.fuzzy.subsystem.gameserver.model.L2Skill;
import com.fuzzy.subsystem.gameserver.serverpackets.Die;
import com.fuzzy.subsystem.gameserver.templates.L2NpcTemplate;

public class L2DeadManInstance extends L2MonsterInstance
{
	public L2DeadManInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
	}

	/**
	 * Return the L2CharacterAI of the L2Character and if its null create a new one.<BR><BR>
	 */
	@Override
	public L2CharacterAI getAI()
	{
		if(_ai == null)
			_ai = new L2CharacterAI(this);
		return _ai;
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();
		setCurrentHp(0, false);
		setDead(true);
		broadcastStatusUpdate();
		broadcastPacket(new Die(this));
		setWalking();
	}

	@Override
	public void reduceCurrentHp(double damage, L2Character attacker, L2Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean isDot, double i2, boolean sendMesseg, boolean bow, boolean crit, boolean tp)
	{}

	@Override
	public void doDie(L2Character killer)
	{}

	@Override
	public int getAggroRange()
	{
		return 0;
	}
}