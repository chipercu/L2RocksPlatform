package com.fuzzy.subsystem.gameserver.skills.pts_effects;

import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.model.L2Effect;
import com.fuzzy.subsystem.gameserver.serverpackets.FlyToLocation;
import com.fuzzy.subsystem.gameserver.serverpackets.FlyToLocation.FlyType;
import com.fuzzy.subsystem.gameserver.serverpackets.ValidateLocation;
import com.fuzzy.subsystem.gameserver.skills.Env;
import com.fuzzy.subsystem.gameserver.skills.effects.EffectTemplate;
import com.fuzzy.subsystem.util.Location;
/**
 * {i_fly_away;curve;push;300}
 * @i_fly_away
 **/
/**
 * @author : Diagod
 **/
public class i_fly_away extends L2Effect
{
	public i_fly_away(Env env, EffectTemplate template, Integer flyRadius)
	{
		super(env, template);

		_flyRadius = flyRadius;
	}

	private int _x, _y, _z;
	private int _flyRadius;

	@Override
	public void onStart()
	{
		super.onStart();
		final int curX = getEffected().getX();
		final int curY = getEffected().getY();
		final int curZ = getEffected().getZ();

		double dx = getEffector().getX() - curX;
		double dy = getEffector().getY() - curY;
		double dz = getEffector().getZ() - curZ;
		double distance = Math.sqrt((dx * dx) + (dy * dy));

		int offset = Math.min((int) distance + _flyRadius, 1400);
		
		double cos;
		double sin;

		offset += Math.abs(dz);
		if(offset < 5)
			offset = 5;

		if(distance < 1)
			return;

		sin = dy / distance;
		cos = dx / distance;

		_x = getEffector().getX() - (int) (offset * cos);
		_y = getEffector().getY() - (int) (offset * sin);
		_z = getEffected().getZ();

		Location destiny = GeoEngine.moveCheckInAir(getEffected().getX(), getEffected().getY(), getEffected().getZ(), _x, _y, _z, getEffected().getColRadius(), getEffected().getReflection().getGeoIndex());
		_x = destiny.x;
		_y = destiny.y;

		getEffected().startStunning();
		getEffected().broadcastPacket(new FlyToLocation(getEffected(), new Location(_x, _y, _z), FlyType.THROW_UP));
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onExit()
	{
		getEffected().stopStunning();
		getEffected().setXYZ(_x, _y, _z);
		getEffected().broadcastPacket(new ValidateLocation(getEffected()));
	}
}