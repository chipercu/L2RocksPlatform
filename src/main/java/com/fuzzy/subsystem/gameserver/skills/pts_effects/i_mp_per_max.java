package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_mp_per_max;63}
 * @i_mp_per_max
 * @63 - Процент добавляемого МП.
 **/
/**
 * @author : Diagod
 **/
public class i_mp_per_max extends L2Effect
{
	private int _mp;

	public i_mp_per_max(Env env, EffectTemplate template, Integer mp)
	{
		super(env, template);

		_mp = mp;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effected.block_mp.get())
			return;
		double base = Math.max(0, _effected.getMaxMp() * _mp / 100);

		double addToMp = Math.max(0, Math.min(base, _effected.calcStat(Stats.MP_LIMIT, null, null) * _effected.getMaxMp() / 100. - _effected.getCurrentMp()));

		if(_effected.isPlayer())
		{
			if(_effector != _effected)
				_effected.sendPacket(new SystemMessage(SystemMessage.XS2S_MP_HAS_BEEN_RESTORED_BY_S1).addString(_effector.getName()).addNumber(Math.round(addToMp)));
			else
				_effected.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToMp)));
		}

		if(addToMp > 0)
			_effected.setCurrentMp(addToMp + _effected.getCurrentMp());
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}