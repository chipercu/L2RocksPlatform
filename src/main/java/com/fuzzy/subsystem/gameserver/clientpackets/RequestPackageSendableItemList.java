package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.PackageSendableList;

/**
 * Format: cd
 * @author SYS
 */
public class RequestPackageSendableItemList extends L2GameClientPacket
{
	private int _characterObjectId;

	@Override
	public void readImpl()
	{
		_characterObjectId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || !activeChar.getPlayerAccess().UseWarehouse || activeChar.is_block)
			return;
		activeChar.tempInventoryDisable();
		activeChar.sendPacket(new PackageSendableList(activeChar, _characterObjectId));
	}
}