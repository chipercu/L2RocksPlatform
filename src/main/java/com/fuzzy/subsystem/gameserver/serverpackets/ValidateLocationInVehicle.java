package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Location;

public class ValidateLocationInVehicle extends L2GameServerPacket
{
	private int _charObjId, _boatObjId;
	private Location _loc;

	public ValidateLocationInVehicle(L2Player player)
	{
		_charObjId = player.getObjectId();
		_boatObjId = player.getVehicle().getObjectId();
		_loc = player.getInVehiclePosition();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x80);
		writeD(_charObjId);
		writeD(_boatObjId);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}