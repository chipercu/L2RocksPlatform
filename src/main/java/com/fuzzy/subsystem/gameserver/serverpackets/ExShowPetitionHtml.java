package com.fuzzy.subsystem.gameserver.serverpackets;

public class ExShowPetitionHtml extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xB1);
		// TODO dx[dcS]
	}
}