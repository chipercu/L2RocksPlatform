package com.fuzzy.subsystem.gameserver.clientpackets;

public class RequestExCleftEnter extends L2GameClientPacket
{
	private int unk;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk);
	}

	/**
	 * format: d
	 */
	@Override
	public void readImpl()
	{
		unk = readD();
	}
}