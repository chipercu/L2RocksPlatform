package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class EffectParalyze extends L2Effect
{
	public EffectParalyze(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isParalyzeImmune())
			return false;
		if(_effected.isParalyzed())
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		_effected.setParalyzedSkill(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.setParalyzedSkill(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}