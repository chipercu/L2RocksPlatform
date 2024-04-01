package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.FinishRotating;
import com.fuzzy.subsystem.gameserver.serverpackets.StartRotating;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.Formulas;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;

/**
 * {i_align_direction;80}
 * @i_align_direction
 * @80 - шанс прохождения эффекта, расчитывается по общей формуле дебафов.
 **/
/**
 * @author : Diagod
 **/
public class i_align_direction extends L2Effect
{
	public i_align_direction(Env env, EffectTemplate template, Integer chance)
	{
		super(env, template);
		_instantly = true;
		env.value = chance;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(Formulas.calcSkillSuccess(_env, getEffector().getChargedSpiritShot(), false))
		{
			getEffected().broadcastPacket2(new StartRotating(getEffected(), getEffected().getHeading(), 1, 65535));
			getEffected().broadcastPacket2(new FinishRotating(getEffected(), getEffector().getHeading(), 65535));
			getEffected().setHeading(getEffector().getHeading());
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}