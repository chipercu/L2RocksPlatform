package com.fuzzy.subsystem.gameserver.serverpackets;

public class ExPeriodicItemList extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0x87);
		writeD(0); // count of dd
	}
}