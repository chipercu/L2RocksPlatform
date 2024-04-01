package com.fuzzy.subsystem.gameserver.serverpackets;

public class ExResponseShowStepOne extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(EXTENDED_PACKET);
		writeHG(0xAE);
		// TODO dx[cS]
	}
}