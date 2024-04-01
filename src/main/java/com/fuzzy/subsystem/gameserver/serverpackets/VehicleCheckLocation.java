package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2Ship;
import com.fuzzy.subsystem.util.Location;

public class VehicleCheckLocation extends L2GameServerPacket
{
	private int _boatObjId;
	private Location _loc;

	public VehicleCheckLocation(L2Ship instance)
	{
		_boatObjId = instance.getObjectId();
		_loc = instance.getLoc();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x6d);
		writeD(_boatObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}