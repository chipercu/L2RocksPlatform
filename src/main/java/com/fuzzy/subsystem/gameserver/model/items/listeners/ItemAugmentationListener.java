package com.fuzzy.subsystem.gameserver.model.items.listeners;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.Inventory;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;

public final class ItemAugmentationListener implements PaperdollListener
{
	private Inventory _inv;

	public ItemAugmentationListener(Inventory inv)
	{
		_inv = inv;
	}

	public void notifyUnequipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		if(item.isAugmented())
		{
			L2Player player = _inv.getOwner().getPlayer();
			item.getAugmentation().removeBoni(player, false);
		}
		if(item.getItem().getAttachedTriggers() != null)
		{
			L2Player player = _inv.getOwner().getPlayer();
			player.removeTrigger(item.getItem().getAttachedTriggers());
		}
	}

	public void notifyEquipped(int slot, L2ItemInstance item, boolean update_icon)
	{
		if(_inv.getOwner() == null || !_inv.getOwner().isPlayer() || !item.isEquipable())
			return;

		if(item.isAugmented())
		{
			L2Player player = _inv.getOwner().getPlayer();
			item.getAugmentation().applyBoni(player, false);
		}

		if(item.getItem().getAttachedTriggers() != null)
		{
			L2Player player = _inv.getOwner().getPlayer();
			player.addTrigger(item.getItem().getAttachedTriggers());
		}
	}
}