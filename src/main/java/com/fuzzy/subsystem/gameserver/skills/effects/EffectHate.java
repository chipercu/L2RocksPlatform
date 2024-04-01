package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class EffectHate extends L2Effect
{
	public EffectHate(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		if(_effected.isNpc() && _effected.isMonster())
			_effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _effector, _template._value);
	}

	/*@Override
	public boolean isHidden()
	{
		return true;
	}*/

	@Override
	public boolean onActionTime()
	{
		return false;
	}	
}