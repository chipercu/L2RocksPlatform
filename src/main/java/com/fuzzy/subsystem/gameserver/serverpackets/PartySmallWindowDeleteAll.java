package com.fuzzy.subsystem.gameserver.serverpackets;

public class PartySmallWindowDeleteAll extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x50);
	}
}