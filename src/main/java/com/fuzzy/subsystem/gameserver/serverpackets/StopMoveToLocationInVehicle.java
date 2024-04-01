package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.util.Location;

public class StopMoveToLocationInVehicle extends L2GameServerPacket
{
	private int _boatid, char_obj_id, char_heading;
	private Location _loc;

	public StopMoveToLocationInVehicle(L2Player player, int boatid)
	{
		_boatid = boatid;
		char_obj_id = player.getObjectId();
		_loc = player.getInVehiclePosition();
		char_heading = player.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x7f);
		writeD(char_obj_id);
		writeD(_boatid);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z);
		writeD(char_heading);
	}
}