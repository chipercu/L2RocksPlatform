package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {c_fake_death;-63;5}
 * @c_fake_death
 * @-63 - Количество МП на Тик...
 * @5 - Время тика(666мс 1 тик)
 **/
/**
 * @author : Diagod
 **/
public class c_fake_death extends c_mp
{
	public c_fake_death(Env env, EffectTemplate template, Double mp_tick, Integer tick_time)
	{
		super(env, template, mp_tick, tick_time);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().startFakeDeath();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		// 5 секунд после FakeDeath на персонажа не агрятся мобы
		getEffected().setNonAggroTime(System.currentTimeMillis() + 5000);
		getEffected().stopFakeDeath();
	}
}