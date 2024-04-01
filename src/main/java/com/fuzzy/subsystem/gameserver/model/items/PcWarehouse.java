package com.fuzzy.subsystem.gameserver.model.items;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance.ItemLocation;

public class PcWarehouse extends Warehouse
{
	private final L2Player _owner;

	public PcWarehouse(L2Player owner)
	{
		_owner = owner;
	}

	public L2Player getOwner()
	{
		return _owner;
	}

	@Override
	public int getOwnerId()
	{
		L2Player owner = getOwner();
		return owner == null ? 0 : owner.getObjectId();
	}

	@Override
	public ItemLocation getLocationType()
	{
		return ItemLocation.WAREHOUSE;
	}
}