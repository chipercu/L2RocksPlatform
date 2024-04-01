package com.fuzzy.subsystem.gameserver.clientpackets;

/**
 * Format chS
 * c: (id) 0x39
 * h: (subid) 0x00
 * S: the character name (or maybe cmd string ?)
 */
class SuperCmdCharacterInfo extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private String _characterName;

	@Override
	public void readImpl()
	{
		_characterName = readS();
	}

	@Override
	public void runImpl()
	{}
}