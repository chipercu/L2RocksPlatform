package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.ai.*;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_get_agro}
 * @i_get_agro
 **/
/**
 * @author : Diagod
 **/
public class i_get_agro extends L2Effect
{
	public i_get_agro(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isNpc())
		{
			_effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getEffector(), getSkill().getEffectPoint());
			_effected.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, new Object[] { getEffector(), 0, getSkill() });
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}