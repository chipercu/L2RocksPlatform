package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.SkillAbnormalType;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_dispel_by_slot;ma_up;9}
 * @i_dispel_by_slot
 * @ma_up - abnormal_type с которым будут сняты бафы/дебафы.
 * @9 - abnormal_lv уровень включительно, по какой будут сниматся бафы/дебафы.
 **/
/**
 * @author : Diagod
 **/
public class i_dispel_by_slot extends L2Effect
{
	private SkillAbnormalType _sat;
	private int _lvl;

	public i_dispel_by_slot(Env env, EffectTemplate template, SkillAbnormalType sat, Integer lvl)
	{
		super(env, template);
		_sat = sat;
		_lvl = lvl;
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		for(L2Effect e : _effected.getEffectList().getAllEffects())
			if(e.getAbnormalType() == _sat && (e.getAbnormalLv() <= _lvl || _lvl == -1 || _lvl >= 9))
			{
				e.setCanDelay(false);
				e.exit(false, false);
				update_effect_list = true;
			}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}