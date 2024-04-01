package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.*;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
public class p_fishing_mastery extends L2Effect
{
	public p_fishing_mastery(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
