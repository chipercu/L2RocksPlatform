package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.*;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * @author : Diagod
 **/
 /**
 * {p_change_fishing_mastery;2;0.9375}
 **/
public class p_change_fishing_mastery extends L2Effect
{
	public p_change_fishing_mastery(Env env, EffectTemplate template, Integer level, Double unk)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}
