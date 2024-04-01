package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.gameserver.skills.funcs.*;

/**
 * {p_magical_defence_add;{all};30;per}
 **/
/**
 * @author : Diagod
 **/
public class p_magical_defence_add extends L2Effect
{
	private double _val;
	private FuncCalc _func;

	public p_magical_defence_add(Env env, EffectTemplate template, String cond, Double value, FuncCalc func)
	{
		super(env, template);

		_val = value;
		_func = func;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		/*if(_func == FuncCalc.diff)
			getEffected().getStat().p_magical_defence_diff(_val);
		else
			getEffected().getStat().p_magical_defence_per(_val);*/
	}

	@Override
	public void onExit()
	{
		super.onExit();
		/*if(_func == FuncCalc.diff)
			getEffected().getStat().p_magical_defence_diff(-_val);
		else
			getEffected().getStat().p_magical_defence_per(-_val);*/
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}