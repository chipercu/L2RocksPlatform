package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2ObjectsStorage;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.ExMPCCShowPartyMemberInfo;

/**
 * Format: ch d
 * Пример пакета:
 * D0 2E 00 4D 90 00 10
 * @author SYS
 */
public class RequestExMPCCShowPartyMembersInfo extends L2GameClientPacket
{
	private int _objectId;

	@Override
	public void readImpl()
	{
		_objectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || !activeChar.isInParty() || !activeChar.getParty().isInCommandChannel())
			return;

		L2Player partyLeader = L2ObjectsStorage.getPlayer(_objectId);
		if(partyLeader != null)
			activeChar.sendPacket(new ExMPCCShowPartyMemberInfo(partyLeader));
	}
}