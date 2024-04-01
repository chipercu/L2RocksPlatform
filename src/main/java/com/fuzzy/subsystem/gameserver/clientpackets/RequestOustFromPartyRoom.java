package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.instancemanager.PartyRoomManager;
import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.PartyRoom;

/**
 * format (ch) d
 */
public class RequestOustFromPartyRoom extends L2GameClientPacket
{
	private int _id;

	@Override
	public void readImpl()
	{
		_id = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2Player member = L2ObjectsStorage.getPlayer(_id);
		if(activeChar == null || member == null || activeChar.is_block)
			return;

		PartyRoom room = PartyRoomManager.getInstance().getRoom(member.getPartyRoom());
		if(room != null)
			room.removeMember(member, true);
	}
}