package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.L2Summon;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_npc_kill;80}
 * @i_npc_kill
 * @80 - шанс.
 **/
public class i_npc_kill extends L2Effect
{
	public i_npc_kill(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isSummon() && _effected.getLevel() - getSkill().getMagicLevel() <= 9 && Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
			((L2Summon) _effected).unSummon();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}