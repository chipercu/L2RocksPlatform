package com.fuzzy.subsystem.gameserver.serverpackets;

public class ShopPreviewInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xF6);
		// TODO d{ddddddddddddddddddddddddddddd}
	}
}