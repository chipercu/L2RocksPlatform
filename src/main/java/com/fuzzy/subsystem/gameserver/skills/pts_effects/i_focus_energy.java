package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.*;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_focus_energy;7}
 * @i_focus_energy
 * @7 - максимальное количество зарядов.
 **/
/**
 * @author : Diagod
 **/
public class i_focus_energy extends L2Effect
{
	private Integer _charges;

	public i_focus_energy(Env env, EffectTemplate template, Integer charges)
	{
		super(env, template);
		_charges = charges;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(getEffected().isPlayer() && getEffected().getPlayer().getIncreasedForce() < _charges)
			getEffected().getPlayer().setIncreasedForce(getEffected().getPlayer().getIncreasedForce() + 1);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}