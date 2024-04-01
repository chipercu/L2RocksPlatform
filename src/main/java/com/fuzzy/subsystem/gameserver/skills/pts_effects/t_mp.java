package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.SkillAbnormalType;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncPTS;

/**
 * {t_mp;-63;5;diff}
 * @t_mp
 * @-63 - Количество Мп на Тик...
 * @5 - Время тика(666мс 1 тик)
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class t_mp extends L2Effect
{
	private double _mp_tick;
	private FuncPTS _act;

	public t_mp(Env env, EffectTemplate template, Double mp_tick, Integer tick_time, FuncPTS act)
	{
		super(env, template);

		_mp_tick = mp_tick;
		_tick_time = tick_time;
		_act = act;
		isDot = true;
	}

	/**
		if(target.isDead() || target.isHealBlocked(true) || target.block_mp.get())
			continue;
	**/
	@Override
	public boolean onActionTime()
	{
		if(_effected.block_mp.get())
			return false;
		else if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || getSkill().isToggle() || getAbnormalType() == SkillAbnormalType.mp_recover))
		{
			_effected.getEffectList().stopEffect(getSkill().getId());
			return false;
		}
		double damage = _act.calc(ConfigValue.DotModifer * _mp_tick * _tick_time, _effected.getMaxMp());

		if(damage > 0)
		{
			if(_effected.isHealBlocked(true, false))
			{
				_effected.getEffectList().stopEffect(getSkill().getId()); // ??????
				return false;
			}
			damage = Math.max(0, damage);
		}

		_effected.setCurrentMp(_effected.getCurrentMp() + damage);
		return true;
	}
}