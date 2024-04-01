package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_consume_body}
 * @i_consume_body
 **/
/**
 * @author : Diagod
 **/
public class i_consume_body extends L2Effect
{
	public i_consume_body(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffector().getCastingTarget() != null && !getEffector().getCastingTarget().isPlayer())
		{
			getEffector().getAI().setAttackTarget(null);
			getEffector().getCastingTarget().endDecayTask();
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}