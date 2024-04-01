package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.SkillAbnormalType;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_buff_slot;{ma_up;md_up}}
 * @p_block_buff_slot
 * @{ma_up;md_up} - список abnormal_type, которые не смогут наложится на чара.
 **/
public class p_block_buff_slot extends L2Effect
{
	private SkillAbnormalType[] _sat;
	public p_block_buff_slot(Env env, EffectTemplate template, SkillAbnormalType[] sat)
	{
		super(env, template);
		_sat = sat;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().addBlockBuffSlot(_sat);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().removeBlockBuffSlot(_sat);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}