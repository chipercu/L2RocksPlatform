package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlIntention;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.model.L2Character.HateInfo;
import com.fuzzy.subsystem.gameserver.model.instances.L2NpcInstance;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_randomize_hate;80}
 * @i_randomize_hate
 * @80 - шанс прохождения эффекта.
 * Выбирает рандомную цель из своего агро листа, если там только 1 цель, то продолжает лупить ее...
 * Только вот не пойму, он берет максХейт у кого или рандомно из всего списка...
 **/
/**
 * @author : Diagod
 **/
public class i_randomize_hate extends L2Effect
{
	public i_randomize_hate(Env env, EffectTemplate template, Integer chance)
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
			L2NpcInstance monster = (L2NpcInstance)getEffected();
			//L2Character most_hated = monster.getAI().getAttackTarget();
			L2Character most_hated = monster.getMostHated();
			if(most_hated == null)
				return;
			HateInfo hate_max = most_hated.getHateList().get(monster);
			L2Character target = monster.getRandomHated();
			HateInfo hate_select = target.getHateList().get(monster);
			if(target == null)
				return;

			if(target != null && target != monster)
			{
				final int select_hate = hate_select.hate;
				final int max_hate = hate_max.hate;

				hate_select.hate = max_hate;
				hate_max.hate = select_hate;
				
				monster.getAI().setAttackTarget(target);
				monster.getAI().changeIntention(CtrlIntention.AI_INTENTION_ATTACK, target, null);
				monster.getAI().addTaskAttack(target);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}