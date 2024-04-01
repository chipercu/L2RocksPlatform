package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;


public final class p_ignore_skill extends L2Effect
{
	Integer[] value;
	public p_ignore_skill(Env env, EffectTemplate template)
	{
		super(env, template);

		value = new Integer[template._effect_param.length];
		for(int i = 0; i < template._effect_param.length; i++)
			value[i] = Integer.parseInt(template._effect_param[i]);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().addBlockSkill(value);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().removeBlockSkill(value);
	}
}