package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {p_block_getdamage;block_hp}
 * block_hp
 * block_mp
 **/

public class p_block_getdamage extends L2Effect
{
	boolean block_hp;
	public p_block_getdamage(Env env, EffectTemplate template, Boolean bh)
	{
		super(env, template);
		block_hp = bh;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		getEffected().block_hp_mp(_obj_id, getSkill(), block_hp);
	}

	@Override
	public void onExit()
	{
		super.onExit();
		getEffected().unblock_hp_mp(_obj_id, getSkill(), block_hp);
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}