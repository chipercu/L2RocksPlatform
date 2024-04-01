package com.fuzzy.subsystem.gameserver.clientpackets;

public class RequestExShowStepThree extends L2GameClientPacket
{
	private int unk;

	@Override
	public void runImpl()
	{
		_log.info(getType() + " :: " + unk);
	}

	/**
	 * format: c
	 */
	@Override
	public void readImpl()
	{
		unk = readC();
	}
}