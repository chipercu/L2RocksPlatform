package com.fuzzy.subsystem.gameserver.serverpackets;

/**
 * Close the CommandChannel Information window
 */
public class ExMPCCClose extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeH(0x13);
	}
}
