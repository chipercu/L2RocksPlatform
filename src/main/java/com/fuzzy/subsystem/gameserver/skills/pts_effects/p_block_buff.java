package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_buff}
 **/
public class p_block_buff extends L2Effect
{
	public p_block_buff(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().p_block_buff.getAndSet(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().p_block_buff.setAndGet(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}