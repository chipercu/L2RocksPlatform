package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_dispel_all}
 * @i_dispel_all
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_all extends L2Effect
{
	public i_dispel_all(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		for(L2Effect e : _effected.getEffectList().getAllEffects())
		{
			e.setCanDelay(false);
			e.exit(false, false);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}