package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.clientpackets.RequestExPostItemList;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.Inventory;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ответ на запрос создания нового письма.
 * Отсылается при получении {@link RequestExPostItemList}
 * Содержит список вещей, которые можно приложить к письму.
 */
public class ExReplyPostItemList extends L2GameServerPacket
{
	private List<L2ItemInstance> _itemslist = new ArrayList<L2ItemInstance>();

	public ExReplyPostItemList(L2Player cha)
	{
		if(!cha.getPlayerAccess().UseTrade) // если не разрешен трейд передавать предметы нельзя
			return;

		String tradeBan = cha.getVar("tradeBan"); // если трейд забанен тоже
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
			return;

		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item.canBeTraded(cha))
				_itemslist.add(item);
		Collections.sort(_itemslist, Inventory.OrderComparator);
	}

	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xB2);

		writeD(_itemslist.size());
		for(L2ItemInstance temp : _itemslist)
			writeItemInfo(temp);
	}
}