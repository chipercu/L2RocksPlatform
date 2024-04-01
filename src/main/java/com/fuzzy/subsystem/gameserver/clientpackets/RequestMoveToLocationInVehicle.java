package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2Ship;
import com.fuzzy.subsystem.gameserver.model.entity.vehicle.L2VehicleManager;
import com.fuzzy.subsystem.gameserver.serverpackets.MoveToLocationInVehicle;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private int _boatId;

	private int _tx;
	private int _ty;
	private int _tz;
	private int _ox;
	private int _oy;
	private int _oz;

	/**
	 * format: cddddddd
	 */
	@Override
	public void readImpl()
	{
		_boatId = readD(); // objectId of boat
		_tx = readD();
		_ty = readD();
		_tz = readD();
		_ox = readD();
		_oy = readD();
		_oz = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getPet() != null)
		{
			activeChar.sendPacket(Msg.BECAUSE_PET_OR_SERVITOR_MAY_BE_DROWNED_WHILE_THE_BOAT_MOVES_PLEASE_RELEASE_THE_SUMMON_BEFORE_DEPARTURE, Msg.ActionFail);
			return;
		}

		if(activeChar.getTransformation() != 0)
		{
			activeChar.sendPacket(Msg.YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED, Msg.ActionFail);
			return;
		}

		if(activeChar.isMovementDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2Ship boat = (L2Ship) L2VehicleManager.getInstance().getBoat(_boatId);
		if(boat == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setInVehiclePosition(_tx, _ty, _tz);
		activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, boat, _ox, _oy, _oz, _tx, _ty, _tz));
	}
}