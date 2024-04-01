package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2AirShip;
import com.fuzzy.subsystem.util.Location;

public class ExStopMoveInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id, char_heading;
	private Location _loc;

	/*
	 * структура пакета не точна, это лишь предположения 
	 */
	public ExStopMoveInAirShip(L2Player cha, L2AirShip boat)
	{
		char_id = cha.getObjectId();
		boat_id = boat.getObjectId();
		_loc = cha.getInVehiclePosition(); //?!
		char_heading = cha.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x6E);

		writeD(char_id);
		writeD(boat_id);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(char_heading);
	}
}