package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class EffectGrow extends L2Effect
{
	public EffectGrow(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			npc.setCollisionHeight(npc.getCollisionHeight() * 1.24);
			npc.setCollisionRadius(npc.getCollisionRadius() * 1.19);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.isNpc())
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			npc.setCollisionHeight(npc.getTemplate().collisionHeight);
			npc.setCollisionRadius(npc.getTemplate().collisionRadius);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}