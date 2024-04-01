package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.ai.DefaultAI;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class EffectEnervation extends L2Effect
{
	public EffectEnervation(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.hasAI() && _effected.getAI() instanceof DefaultAI)
			((DefaultAI) _effected.getAI()).DebuffIntention = 0.5f;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected.hasAI() && _effected.getAI() instanceof DefaultAI)
			((DefaultAI) _effected.getAI()).DebuffIntention = 1.0f;
	}
}