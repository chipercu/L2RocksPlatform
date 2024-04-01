package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlIntention;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_delete_hate;40}
 * @i_delete_hate
 * @40 - шанс срабатывания.
 **/
/**
 * @author : Diagod
 **/
public class i_delete_hate extends L2Effect
{
	public i_delete_hate(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc() && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			npc.clearAggroList(true);
			npc.getAI().clearTasks();
			npc.getAI().setGlobalAggro(System.currentTimeMillis() + 10000);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}