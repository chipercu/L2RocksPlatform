package com.fuzzy.subsystem.gameserver.serverpackets;

public class DismissAlliance extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xAD);
		//TODO d
	}
}