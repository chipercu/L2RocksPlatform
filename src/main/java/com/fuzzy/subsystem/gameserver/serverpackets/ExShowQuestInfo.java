package com.fuzzy.subsystem.gameserver.serverpackets;

public class ExShowQuestInfo extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x20);
	}
}