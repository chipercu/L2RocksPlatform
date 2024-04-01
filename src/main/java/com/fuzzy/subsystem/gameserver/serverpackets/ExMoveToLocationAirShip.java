package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2AirShip;
import com.fuzzy.subsystem.util.Location;

public class ExMoveToLocationAirShip extends L2GameServerPacket
{
	private int _boatObjId;
	private Location _origin, _destination;

	public ExMoveToLocationAirShip(L2AirShip boat, Location origin, Location destination)
	{
		_boatObjId = boat.getObjectId();
		_origin = origin;
		_destination = destination;
	}

	@Override
	protected final void writeImpl()
	{
		if(_destination == null)
			return;

		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x66 : 0x65);
		writeD(_boatObjId);

		writeD(_destination.x);
		writeD(_destination.y);
		writeD(_destination.z);
		writeD(_origin.x);
		writeD(_origin.y);
		writeD(_origin.z);
	}
}