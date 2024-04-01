package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.config.*;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public final class EffectBlessNoblesse extends L2Effect
{
	public EffectBlessNoblesse(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().setIsBlessedByNoblesse(true);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(ConfigValue.DeleteNoblesseBlessing)
			getEffected().setIsBlessedByNoblesse(false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}