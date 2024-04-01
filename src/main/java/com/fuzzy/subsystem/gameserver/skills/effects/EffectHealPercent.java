package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;
import com.fuzzy.subsystem.gameserver.skills.Env;

public class EffectHealPercent extends L2Effect
{
	public EffectHealPercent(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effector.isPlayable() && _effected.isMonster())
			return false;
		return super.checkCondition();
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && (!_effected.isBlessedByNoblesse() && !_effected._blessed || _effected.isHealBlocked(true, false)))
			return false;
		else if(!_effected.isDead() && !_effected.block_hp.get())
		{
			double base = calc() * _effected.getMaxHp() / 100;
			double addToHp = Math.max(0, base);
			if(addToHp > 0)
				addToHp = _effected.setCurrentHp(addToHp + _effected.getCurrentHp(), false);
			_effected.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
		}
		return false;
	}
}