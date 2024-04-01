package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.EffectType;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Stats;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.FuncPTS;

/**
 * {t_hp_fatal;-63;5;diff}
 * @t_hp_fatal
 * @-63 - Количество ХП на Тик...
 * @5 - Время тика(666мс 1 тик)
 * @diff - что делаем, добавляем статик или процент.
 **/
/**
 * @author : Diagod
 **/
public class t_hp_fatal extends L2Effect
{
	private double _hp_tick;
	private FuncPTS _act;

	public t_hp_fatal(Env env, EffectTemplate template, Double hp_tick, Integer tick_time, FuncPTS act)
	{
		super(env, template);

		_hp_tick = hp_tick;
		_tick_time = tick_time;
		_act = act;
		isDot = true;
	}

	@Override
	public boolean onActionTime()
	{
		double damage = _act.calc(-ConfigValue.DotModifer * _hp_tick * _tick_time, _effected.getMaxHp());

		damage = _effector.calcStat(getSkill().isMagic() ? Stats.MAGIC_DAMAGE : Stats.PHYSICAL_DAMAGE, damage, _effected, getSkill());

		if(_effected.isInvisible() && _effected.getEffectList().getEffectByType(EffectType.p_hide) != null)
			_effected.getEffectList().stopAllSkillEffects(EffectType.p_hide);

		if(_effected == _effector && damage > _effected.getCurrentHp() - 1)
			damage = _effected.getCurrentHp() - 1;

		_effected.reduceCurrentHp(damage, _effector, getSkill(), !_effected.isNpc() && _effected != _effector, _effected != _effector, _effector.isNpc() || getSkill().isToggle() || _effected == _effector, false, true, damage, true, false, false, false);

		return true;
	}
}