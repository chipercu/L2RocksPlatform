package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.*;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncPTS;

/**
 * {p_passive}
 * @p_passive
 **/
/**
 * @author : Diagod
 **/
public class p_max_cp extends L2Effect
{
	public p_max_cp(Env env, EffectTemplate template, String cond, int cp_add, FuncPTS func, byte is_heal)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}