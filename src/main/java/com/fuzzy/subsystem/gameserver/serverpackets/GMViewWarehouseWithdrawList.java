package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance.ItemClass;

public class GMViewWarehouseWithdrawList extends L2GameServerPacket
{
	private final L2ItemInstance[] _items;
	private String _charName;
	private long _charAdena;

	public GMViewWarehouseWithdrawList(L2Player cha)
	{
		_charName = cha.getName();
		_charAdena = cha.getAdena();
		_items = cha.getWarehouse().listItems(ItemClass.ALL);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x9b);
		writeS(_charName);
		writeQ(_charAdena);
		writeH(_items.length);
		for(L2ItemInstance temp : _items)
		{
			writeItemInfo(temp);
			writeD(temp.getObjectId());
		}
	}
}