package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class EffectSleep extends L2Effect
{
	public EffectSleep(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.startSleeping(getSkill());
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopSleeping(getSkill());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}