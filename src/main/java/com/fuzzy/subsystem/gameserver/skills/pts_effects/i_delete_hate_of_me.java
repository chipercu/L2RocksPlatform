package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlIntention;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_delete_hate_of_me;80}
 * @i_delete_hate_of_me
 * @80 - шанс прохождения эффекта, расчитывается по общей формуле дебафов.
 * Работает только на НПС...
 **/
/**
 * @author : Diagod
 **/
public class i_delete_hate_of_me extends L2Effect
{
	public i_delete_hate_of_me(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isNpc() && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			L2NpcInstance npc = (L2NpcInstance) _effected;
			_effector.removeFromHatelist(npc, true);
			npc.abortAttack(true, false);
			npc.abortCast(true);
			npc.getAI().clearTasks();
			npc.getAI().setAttackTarget(null);
			if(npc.isNoTarget())
			{
				npc.getAI().setGlobalAggro(System.currentTimeMillis() + 10000);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}