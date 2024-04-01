package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.geodata.GeoEngine;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2AirShip;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2VehicleManager;
import com.fuzzy.subsystem.gameserver.serverpackets.ExGetOffAirShip;
import com.fuzzy.subsystem.util.Location;
import com.fuzzy.subsystem.util.Util;

public class RequestExGetOffAirShip extends L2GameClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _id;

	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_id = readD();
	}

	@Override
	protected void runImpl()
	{
		//_log.info("[T1:ExGetOffAirShip] x: " + _x);
		//_log.info("[T1:ExGetOffAirShip] y: " + _y);
		//_log.info("[T1:ExGetOffAirShip] z: " + _z);
		//_log.info("[T1:ExGetOffAirShip] ship ID: " + _id);

		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2AirShip boat = (L2AirShip) L2VehicleManager.getInstance().getBoat(_id);
		if(boat == null || boat.isMoving) // Не даем слезть с лодки на ходу
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setVehicle(null);

		double angle = Util.convertHeadingToDegree(activeChar.getHeading());
		double radian = Math.toRadians(angle - 90);

		int x = _x - (int) (100 * Math.sin(radian));
		int y = _y + (int) (100 * Math.cos(radian));
		int z = GeoEngine.getHeight(x, y, _z, activeChar.getReflection().getGeoIndex());

		activeChar.setXYZ(x, y, z);
		activeChar.broadcastPacket(new ExGetOffAirShip(activeChar, boat, new Location(x, y, z)));
	}
}