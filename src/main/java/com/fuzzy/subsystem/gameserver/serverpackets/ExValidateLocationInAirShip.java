package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Location;

public class ExValidateLocationInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id;
	private Location _loc;

	/*
	 * структура пакета не точна, это лишь предположения 
	 */
	public ExValidateLocationInAirShip(L2Player cha)
	{
		char_id = cha.getObjectId();
		boat_id = cha.getVehicle().getObjectId();
		_loc = cha.getInVehiclePosition();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x6F);

		writeD(char_id);
		writeD(boat_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(_loc.h);
	}
}