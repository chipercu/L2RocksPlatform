package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.serverpackets.ExPutItemResultForVariationCancel;

public class RequestConfirmCancelItem extends L2GameClientPacket
{
	// format: (ch)d
	int _itemId;

	@Override
	public void readImpl()
	{
		_itemId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		L2ItemInstance item = activeChar.getInventory().getItemByObjectId(_itemId);

		if(item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(!item.isAugmented())
		{
			activeChar.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM);
			return;
		}

		activeChar.sendPacket(new ExPutItemResultForVariationCancel(item, RequestRefineCancel.getRemovalPrice(item.getItem())));
	}
}