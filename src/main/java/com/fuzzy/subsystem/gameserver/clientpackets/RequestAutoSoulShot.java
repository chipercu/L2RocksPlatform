package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.handler.IItemHandler;
import com.fuzzy.subsystem.gameserver.handler.ItemHandler;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.ExAutoSoulShot;
import com.fuzzy.subsystem.gameserver.serverpackets.SystemMessage;

/**
 * format:		chdd
 * @param decrypt
 */
public class RequestAutoSoulShot extends L2GameClientPacket
{
	private int _itemId;
	private boolean _type; // 1 = on : 0 = off;

	@Override
	public void readImpl()
	{
		_itemId = readD();
		_type = readD() == 1;
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
		else if(activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE || activeChar.isInTransaction() || activeChar.isDead())
			return;
		else if(activeChar.isOutOfControl() || activeChar.isBlockUseItem())
		{
			activeChar.sendActionFailed();
			return;
		}

		L2ItemInstance item = activeChar.getInventory().getItemByItemId(_itemId);

		if(item == null)
			return;
		else if(_type)
		{
			activeChar.addAutoSoulShot(_itemId);
			activeChar.sendPacket(new ExAutoSoulShot(_itemId, true));
			activeChar.sendPacket(new SystemMessage(SystemMessage.THE_USE_OF_S1_WILL_NOW_BE_AUTOMATED).addString(item.getName()));
			IItemHandler handler = ItemHandler.getInstance().getItemHandler(_itemId);
			handler.useItem(activeChar, item, false);
			return;
		}

		activeChar.removeAutoSoulShot(_itemId);
		activeChar.sendPacket(new ExAutoSoulShot(_itemId, false));
		activeChar.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addString(item.getName()));
	}
}