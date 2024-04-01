package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2Ship;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2VehicleManager;
import com.fuzzy.subsystem.gameserver.serverpackets.GetOffVehicle;

public class RequestGetOffVehicle extends L2GameClientPacket
{
	// Format: cdddd
	private int _id, _x, _y, _z;

	@Override
	public void readImpl()
	{
		_id = readD();
		_x = readD();
		_y = readD();
		_z = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		L2Ship boat = (L2Ship) L2VehicleManager.getInstance().getBoat(_id);
		if(boat == null || boat.isMoving) // Не даем слезть с лодки на ходу
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setLastClientPosition(null);
		activeChar.setLastServerPosition(null);
		activeChar.setVehicle(null);

		activeChar.broadcastPacket(new GetOffVehicle(activeChar, boat, _x, _y, _z));
	}
}