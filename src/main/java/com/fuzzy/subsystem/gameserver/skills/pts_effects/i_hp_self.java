package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_hp_self;-63}
 * @i_hp_self
 * @-63 - Количество добавляемого ХП.
 **/
/**
 * @author : Diagod
 **/
public class i_hp_self extends L2Effect
{
	private double _hp;

	public i_hp_self(Env env, EffectTemplate template, Double hp)
	{
		super(env, template);

		_hp = hp;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effector.isDead() || _effector.block_hp.get())
			return;

		_effector.setCurrentHp(_effector.getCurrentHp() + _hp, false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}