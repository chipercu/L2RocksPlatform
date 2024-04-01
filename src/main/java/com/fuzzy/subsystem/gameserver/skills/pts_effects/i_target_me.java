package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_target_me;40}
 * @i_target_me
 * @40 - шанс прохождения эффекта.
 **/
/**
 * @author : Diagod
 **/
public class i_target_me extends L2Effect
{
	public i_target_me(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			getEffected().setTarget(getEffector());
			getEffected().getAI().setAttackTarget(getEffector());
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}