package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.FlyToLocation;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.util.Location;

public class EffectWarp extends L2Effect
{
	public EffectWarp(Env env, EffectTemplate template)
	{
		super(env, template);
		_instantly = true;
	}

	@Override
	public void onStart()
	{
		Location flyLoc = _effector.getFlyLocation(_effector, getSkill());
		if(flyLoc != null)
		{
			_effector.setFlyLoc(flyLoc);
			_effector.broadcastPacket(new FlyToLocation(_effector, flyLoc, FlyToLocation.FlyType.DUMMY));
			_effector.setLoc(flyLoc);
		}
		else
		{
			_effector.sendPacket(Msg.CANNOT_SEE_TARGET());
			return;
		}
	}


	@Override
	public boolean onActionTime()
	{
		return false;
	}
}