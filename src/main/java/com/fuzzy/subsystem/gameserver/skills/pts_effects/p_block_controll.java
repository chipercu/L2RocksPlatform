package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_controll}
 **/
/**
 * @author : Diagod
 **/
public class p_block_controll extends L2Effect
{
	public p_block_controll(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startFear();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().stopFear();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}