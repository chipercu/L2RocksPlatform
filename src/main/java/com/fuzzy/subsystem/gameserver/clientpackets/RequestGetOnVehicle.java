package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2Ship;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2VehicleManager;
import com.fuzzy.subsystem.gameserver.serverpackets.GetOnVehicle;

public class RequestGetOnVehicle extends L2GameClientPacket
{
	private int _id;
	private int _tx;
	private int _ty;
	private int _tz;

	/**
	 * packet type id 0x53
	 * format:      cdddd
	 */
	@Override
	public void readImpl()
	{
		_id = readD();
		_tx = readD();
		_ty = readD();
		_tz = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		L2Ship boat = (L2Ship) L2VehicleManager.getInstance().getBoat(_id);
		if(boat == null)
			return;
		activeChar.stopMove();
		activeChar.setVehicle(boat);
		activeChar.setInVehiclePosition(_tx, _ty, _tz);
		activeChar.setLoc(boat.getLoc());
		activeChar.broadcastPacket(new GetOnVehicle(activeChar, boat, _tx, _ty, _tz));
	}
}