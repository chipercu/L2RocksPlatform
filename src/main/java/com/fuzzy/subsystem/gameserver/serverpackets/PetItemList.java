package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.instances.L2PetInstance;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;

public class PetItemList extends L2GameServerPacket
{
	private L2ItemInstance[] items;

	public PetItemList(L2PetInstance cha)
	{
		items = cha.getInventory().getItems();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xb3);
		writeH(items.length);

		for(L2ItemInstance temp : items)
			writeItemInfo(temp);
	}
}