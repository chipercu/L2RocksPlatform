package com.fuzzy.subsystem.gameserver.serverpackets;

public class ActionFail extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeC(0x1f);
	}
}