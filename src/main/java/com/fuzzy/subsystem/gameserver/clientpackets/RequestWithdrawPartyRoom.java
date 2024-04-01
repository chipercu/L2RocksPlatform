package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.PartyRoomManager;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.PartyRoom;

/**
 * Format (ch) dd
 */
public class RequestWithdrawPartyRoom extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _roomId, _data2;

	@Override
	public void readImpl()
	{
		_roomId = readD(); //room id
		_data2 = readD(); //unknown
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		//_log.info("RequestWithdrawPartyRoom from char: " + activeChar.getName());
		//_log.info("Data received: d:" + _roomId + " d:" + _data2);
		PartyRoom room = PartyRoomManager.getInstance().getRoom(_roomId);
		if(room.getLeader() == null || room.getLeader().equals(activeChar))
			PartyRoomManager.getInstance().removeRoom(_roomId);
		else
			PartyRoomManager.getInstance().getRoom(_roomId).removeMember(activeChar, false);
	}
}