package com.fuzzy.subsystem.gameserver.clientpackets;

public class RequestPrivateStoreBuyManage extends L2GameClientPacket
{
	@Override
	public void runImpl()
	{
		_log.info(getType());
	}

	@Override
	public void readImpl()
	{}
}