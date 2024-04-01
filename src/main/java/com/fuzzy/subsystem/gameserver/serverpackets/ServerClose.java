package com.fuzzy.subsystem.gameserver.serverpackets;

public class ServerClose extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0x20);
	}
}