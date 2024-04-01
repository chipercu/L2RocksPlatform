package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Player;

public class PrivateStoreMsgBuy extends L2GameServerPacket
{
	private int char_obj_id;
	private String store_name;

	/**
	 * Название личного магазина покупки
	 * @param player
	 */
	public PrivateStoreMsgBuy(L2Player player)
	{
		char_obj_id = player.getObjectId();
		store_name = player.getTradeList() == null ? "" : player.getTradeList().getBuyStoreName();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xBF);
		writeD(char_obj_id);
		writeS(store_name);
	}
}