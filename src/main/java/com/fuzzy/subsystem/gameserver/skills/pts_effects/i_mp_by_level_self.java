package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_mp_by_level_self;63}
 * @i_mp_by_level_self
 * @63 - Количество добавляемого МП.
 **/
/**
 * @author : Diagod
 **/
public class i_mp_by_level_self extends L2Effect
{
	private double _mp;

	public i_mp_by_level_self(Env env, EffectTemplate template, Double mp)
	{
		super(env, template);

		_mp = mp;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effector.isDead() || _effector.isHealBlocked(true, false))
			return;

		_mp = Math.max(0, Math.min(_mp, _effector.calcStat(Stats.MP_LIMIT, null, null) * _effector.getMaxMp() / 100. - _effector.getCurrentMp()));
		_effector.setCurrentMp(_effector.getCurrentMp() + _mp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}