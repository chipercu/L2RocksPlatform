package com.fuzzy.subsystem.gameserver.skills.effects;

import com.fuzzy.subsystem.gameserver.ai.CtrlEvent;
import com.fuzzy.subsystem.gameserver.ai.CtrlIntention;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.model.L2Summon;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.util.Location;

public final class EffectFear extends L2Effect
{
	public static final int FEAR_RANGE = 900;
	final double angle;

	public EffectFear(Env env, EffectTemplate template)
	{
		super(env, template);
		angle = Math.toRadians(Location.calculateAngleFrom(_effector, _effected));
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isFearImmune())
		{
			getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		// Fear нельзя наложить на осадных саммонов
		if(_effected instanceof L2Summon && ((L2Summon) _effected).isSiegeWeapon())
		{
			getEffector().sendPacket(Msg.THAT_IS_THE_INCORRECT_TARGET());
			return false;
		}

		if(_effected.isInZonePeace())
		{
			getEffector().sendPacket(Msg.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		if(_effected.isSitting())
			_effected.standUp();
		_effected.getAI().clearTasks();
		_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		_effected.startFear();
		onActionTime();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopFear();
		if(!_effected.isPlayable())
		{
			_effected.setRunning();
			_effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _effector, 999);
			_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _effector);
		}
		else
			_effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
	}

	@Override
	public boolean onActionTime()
	{
		if(_effected.isDead() && !_effected.isBlessedByNoblesse() && !_effected._blessed)
			return false;
		else if(!_effected.isDead())
		{
			int oldX = _effected.getX();
			int oldY = _effected.getY();
			int x = oldX + (int)(FEAR_RANGE * Math.cos(angle));
			int y = oldY + (int)(FEAR_RANGE * Math.sin(angle));
			_effected.moveToLocation(GeoEngine.moveCheck(oldX, oldY, _effected.getZ(), x, y, _effected.getReflection().getGeoIndex()), 0, false);
			//_effected.sendMessage("You can feel Fears's effect");
		}
		return true;
	}
}