package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class EffectInterrupt extends L2Effect
{
	public EffectInterrupt(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		if(!getEffected().isRaid() && !getEffected().isEpicRaid())
			getEffected().abortCast(true);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}