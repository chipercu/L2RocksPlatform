package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.model.items.PcInventory;
import com.fuzzy.subsystem.gameserver.serverpackets.ExPutEnchantSupportItemResult;
import com.fuzzy.subsystem.gameserver.templates.L2Item;

public class RequestExTryToPutEnchantSupportItem extends L2GameClientPacket
{
	private int _itemId;
	private int _catalystId;

	@Override
	protected void readImpl()
	{
		_catalystId = readD();
		_itemId = readD();
	}

	@Override
	protected void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance itemToEnchant = inventory.getItemByObjectId(_itemId);
		L2ItemInstance catalyst = inventory.getItemByObjectId(_catalystId);

		if(checkCatalyst(itemToEnchant, catalyst))
		{
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(1));
			activeChar._catalystId=_catalystId;
		}
		else
			activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
	}

	/** 
	 * Проверяет соответствие уровня заточки и вообще катализатор ли это или левый итем
	 * @param itemToEnchant
	 * @param catalyst
	 * @return true если катализатор соответствует
	 */
	public static final boolean checkCatalyst(L2ItemInstance itemToEnchant, L2ItemInstance catalyst)
	{
		if(itemToEnchant == null || catalyst == null)
			return false;

		int current = itemToEnchant.getRealEnchantLevel();
		if(current < (itemToEnchant.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR ? 4 : 3) || current >= 9)
			return false;

		for(int catalystRequired : itemToEnchant.getEnchantCatalystId())
			if(catalystRequired == catalyst.getItemId())
				return true;

		return false;
	}
}