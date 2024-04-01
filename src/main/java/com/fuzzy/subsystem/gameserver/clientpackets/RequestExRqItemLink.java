package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.cache.ItemInfoCache;
import com.fuzzy.subsystem.gameserver.cache.Msg;
import com.fuzzy.subsystem.gameserver.serverpackets.ExRpItemLink;
import com.fuzzy.subsystem.gameserver.serverpackets.ExRpItemLink.ItemInfo;

public class RequestExRqItemLink extends L2GameClientPacket
{
	// format: (ch)d
	int _item;

	@Override
	public void readImpl()
	{
		_item = readD();
	}

	@Override
	public void runImpl()
	{
		ItemInfo item;
		if((item = ItemInfoCache.getInstance().get(_item)) == null)
			sendPacket(Msg.ActionFail);
		else
		{
			sendPacket(new ExRpItemLink(item));
			if(getClient() != null && getClient().getActiveChar() != null && getClient().getActiveChar().isGM())
				getClient().getActiveChar().sendMessage("ItemId: " + item.getItemId());
		}
	}
}