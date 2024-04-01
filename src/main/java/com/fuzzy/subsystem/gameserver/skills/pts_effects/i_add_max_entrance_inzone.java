package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.*;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
public class i_add_max_entrance_inzone extends L2Effect
{
	public i_add_max_entrance_inzone(Env env, EffectTemplate template)
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
