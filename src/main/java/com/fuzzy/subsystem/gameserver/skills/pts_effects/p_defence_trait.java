package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.*;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.SkillTrait;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {p_defence_trait;trait_valakas;20}
 **/
/**
 * @author : Diagod
 **/
public class p_defence_trait extends L2Effect
{
	private SkillTrait _trait;
	private double _val;

	public p_defence_trait(Env env, EffectTemplate template, SkillTrait trait, Double value)
	{
		super(env, template);

		//_log.info("p_defence_trait("+getSkill().getId()+")("+trait+", "+value+")");
		_trait = trait;
		_val = value;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		//_log.info("onStart("+getSkill().getId()+")("+getPeriod()+")("+_val+")");
		if(ConfigValue.DebuffFormulaType == 1)
		{
			double val = _val;
			if(val == 100)
				val = 99d;
			val = (100d - val) / 100d;
			_effected.getTraitStat().modTrait(_trait.ordinal(), val, 1, _val == 100);
		}
		else
			_effected.getTraitStat().modTrait(_trait.ordinal(), _val, 0, _val == 100);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		//_log.info("onExit("+getSkill().getId()+")(-"+_val+")");
		if(ConfigValue.DebuffFormulaType == 1)
		{
			double val = _val;
			if(val == 100)
				val = 99d;
			val = 100d / (100d - val);
			_effected.getTraitStat().modTrait(_trait.ordinal(), val, 1, false);
		}
		else
			_effected.getTraitStat().modTrait(_trait.ordinal(), _val*-1, 0, false);
	}

	@Override
	public boolean onActionTime()
	{
		//_log.info("onActionTime("+getSkill().getId()+")");
		return false;
	}
}