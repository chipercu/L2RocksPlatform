package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.model.items.L2ItemInstance;
import com.fuzzy.subsystem.util.GArray;

/**
 * Format: dQd[hddQhhdhhhddddddddd]
 */
public class PackageSendableList extends L2GameServerPacket
{
	private int player_obj_id;
	private long char_adena;
	private GArray<L2ItemInstance> _itemslist = new GArray<L2ItemInstance>();

	public PackageSendableList(L2Player cha, int playerObjId)
	{
		player_obj_id = playerObjId;
		char_adena = cha.getAdena();
		for(L2ItemInstance item : cha.getInventory().getItems())
			if(item != null && item._is_premium)
				_itemslist.add(item);
	}

	@Override
	protected final void writeImpl()
	{
		if(player_obj_id == 0)
			return;

		writeC(0xD2);
		writeD(player_obj_id);

		writeQ(char_adena);
		writeD(_itemslist.size());
		for(L2ItemInstance temp : _itemslist)
		{
			writeItemInfo(temp);
            writeD(temp.getObjectId());
		}
	}
}