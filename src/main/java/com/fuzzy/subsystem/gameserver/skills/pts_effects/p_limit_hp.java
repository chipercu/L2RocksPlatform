package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.*;

/**
 * {p_limit_hp;30;per}
 **/
/**
 * @author : Diagod
 **/
public class p_limit_hp extends L2Effect
{
	private double _val;

	public p_limit_hp(Env env, EffectTemplate template, Double value)
	{
		super(env, template);

		_val = value;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}