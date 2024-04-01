package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.model.items.PcInventory;
import com.fuzzy.subsystem.gameserver.serverpackets.ExBaseAttributeCancelResult;
import com.fuzzy.subsystem.gameserver.serverpackets.ExShowBaseAttributeCancelWindow;
import com.fuzzy.subsystem.gameserver.serverpackets.InventoryUpdate;
import com.fuzzy.subsystem.gameserver.templates.L2Item;
import com.fuzzy.subsystem.util.Log;

/**
 * @author SYS
 */
public class RequestExRemoveItemAttribute extends L2GameClientPacket
{
	// Format: chd
	private int _objectId, elementId;

    @Override
	public void readImpl()
	{
		_objectId = readD();
        elementId = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isOutOfControl() || activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		PcInventory inventory = activeChar.getInventory();
		L2ItemInstance itemToUnnchant = inventory.getItemByObjectId(_objectId);
		
		long price = ExShowBaseAttributeCancelWindow.getAttributeRemovePrice(itemToUnnchant);

        if(itemToUnnchant == null || !itemToUnnchant.hasAttribute() || activeChar.getPrivateStoreType() != L2Player.STORE_PRIVATE_NONE)
		{
			activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, elementId));
            activeChar.sendActionFailed();
            return;
        }

		if(activeChar.getAdena() < price)
		{
			activeChar.sendPacket(new ExBaseAttributeCancelResult(false, itemToUnnchant, elementId));
			activeChar.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			activeChar.sendActionFailed();
			return;
		}

		activeChar.reduceAdena(price, true);
        if(itemToUnnchant.isWeapon())
            itemToUnnchant.setAttributeElement(L2Item.ATTRIBUTE_NONE, 0, new int[]{0, 0, 0, 0, 0, 0}, true);
        else
		{
            int[] deffAttr = itemToUnnchant.getDeffAttr();
            deffAttr[elementId] = 0;
            itemToUnnchant.setAttributeElement(L2Item.ATTRIBUTE_NONE, 0, deffAttr, true);
        }
		if(itemToUnnchant.isEquipped())
			activeChar.getInventory().refreshListeners(itemToUnnchant, -1);
		else
			activeChar.sendPacket(new InventoryUpdate().addModifiedItem(itemToUnnchant));
		activeChar.sendPacket(new ExBaseAttributeCancelResult(true, itemToUnnchant, elementId));

		activeChar.sendChanges();
		activeChar.sendPacket(new ExShowBaseAttributeCancelWindow(activeChar)); // ???????

		Log.add(activeChar.getName() + "|Successfully unenchanted attribute|" + itemToUnnchant.getItemId(), "enchants");
		Log.LogItem(activeChar, Log.EnchantItem, itemToUnnchant);
	}
}