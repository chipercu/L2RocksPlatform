package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_add_hate;500}
 * @i_add_hate
 * @500 - количество хейта.
 **/
/**
 * @author : Diagod
 **/
public class i_add_hate extends L2Effect
{
	public i_add_hate(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isNpc())
			getEffected().getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getEffector(), (int)calc());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}