package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.L2Summon;
import com.fuzzy.subsystem.gameserver.skills.Env;

import static com.fuzzy.subsystem.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public class EffectBetray extends L2Effect
{
	public EffectBetray(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isSummon() || _effected.isPet())
		{
			L2Summon summon = (L2Summon) _effected;
			summon.setPossessed(true);
			summon.getAI().Attack(summon.getPlayer(), true, false);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();
		if(_effected instanceof L2Summon)
		{
			L2Summon summon = (L2Summon) _effected;
			summon.setPossessed(false);
			summon.getAI().setIntention(AI_INTENTION_ACTIVE);
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}