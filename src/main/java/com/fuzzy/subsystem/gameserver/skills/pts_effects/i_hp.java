package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncPTS;

/**
 * {i_hp;63;diff}
 * @i_hp
 * @-63 - Количество добавляемого ХП.
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class i_hp extends L2Effect
{
	private double _hp;
	private FuncPTS _act;

	public i_hp(Env env, EffectTemplate template, Double hp, FuncPTS act)
	{
		super(env, template);

		_hp = hp;
		_act = act;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isDead() || _effector.block_hp.get())
			return;

		_hp = _act.calc(_hp, _effected.getCurrentHp());

		if(_hp > 0)
		{
			if(_effected.isHealBlocked(true, false))
				return;
		}
		else
		{
			_effected.sendPacket(new SystemMessage(SystemMessage.C1_HAS_DRAINED_YOU_OF_S2_HP).addString(_effector.getName()).addString(String.valueOf((int)_hp)));
		}

		_effected.setCurrentHp(Math.max(1, _effected.getCurrentHp() + _hp), false);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}