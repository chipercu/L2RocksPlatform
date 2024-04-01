package com.fuzzy.subsystem.gameserver.model.entity.siege;

import com.fuzzy.subsystem.util.Location;

public class SiegeSpawn
{
	Location _location;
	private int _npcId;
	private int _siegeUnitId;
	private int _value;

	public SiegeSpawn(int siegeUnitId, Location loc, int npc_id)
	{
		_siegeUnitId = siegeUnitId;
		_location = loc;
		_npcId = npc_id;
	}

	public SiegeSpawn(int siegeUnitId, Location loc, int npc_id, int value)
	{
		_siegeUnitId = siegeUnitId;
		_location = loc;
		_npcId = npc_id;
		_value = value;
	}

	public int getSiegeUnitId()
	{
		return _siegeUnitId;
	}

	public int getNpcId()
	{
		return _npcId;
	}

	public int getValue()
	{
		return _value;
	}

	public Location getLoc()
	{
		return _location;
	}
}