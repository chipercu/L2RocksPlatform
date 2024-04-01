package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_hp_per_max;63}
 * @i_hp_per_max
 * @63 - Процент добавляемого ХП.
 **/
/**
 * @author : Diagod
 **/
public class i_hp_per_max extends L2Effect
{
	private int _hp;

	public i_hp_per_max(Env env, EffectTemplate template, Integer hp)
	{
		super(env, template);

		_hp = hp;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effected.isHealBlocked(true, false) || _effector.block_hp.get())
			return;

		double base = _hp * _effected.getMaxHp() / 100;

		double addToHp = Math.max(0, base);
		if(addToHp > 0)
			addToHp = _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
		_effected.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}