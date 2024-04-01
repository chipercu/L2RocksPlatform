package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2AirShip;

public class ExMoveToLocationInAirShip extends L2GameServerPacket
{
	private int char_id, boat_id;

	private int _dx;
	private int _dy;
	private int _dz;
	private int _x;
	private int _y;
	private int _z;

	public ExMoveToLocationInAirShip(L2Player cha, L2AirShip boat, int x, int y, int z, int dx, int dy, int dz)
	{
		char_id = cha.getObjectId();
		boat_id = boat.getObjectId();

		_x = x;
		_y = y;
		_z = z;
		_dx = dx;
		_dy = dy;
		_dz = dz;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(getClient().isLindvior() ? 0x6E : 0x6D);
		writeD(char_id);
		writeD(boat_id);

		writeD(_dx);
		writeD(_dy);
		writeD(_dz);

		writeD(_x);
		writeD(_y);
		writeD(_z);
	}
}