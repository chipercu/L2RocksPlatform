package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class EffectFreyaSkillIgnore extends L2Effect
{
	public EffectFreyaSkillIgnore(Env env, EffectTemplate template)
	{
		super(env, template);
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
		getEffected().p_ignore_skill_freya=true;
	}
	
	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().p_ignore_skill_freya=false;
	}
}