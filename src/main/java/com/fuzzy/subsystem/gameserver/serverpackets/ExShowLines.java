package com.fuzzy.subsystem.gameserver.serverpackets;

public class ExShowLines extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xA5);
		// TODO hdcc cx[ddd]
	}
}