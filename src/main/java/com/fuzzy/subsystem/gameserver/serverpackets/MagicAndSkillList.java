package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.gameserver.model.L2Character;

/**
 * Format:   ddd
 * Пример пакета:
 * 40
 * c8 22 00 49
 * be 50 00 00
 * 86 25 0b 00
 * @author SYS
 */
public class MagicAndSkillList extends L2GameServerPacket
{
	private int _chaId;
	private int _unk1;
	private int _unk2;

	public MagicAndSkillList(L2Character cha, int unk1, int unk2)
	{
		_chaId = cha.getObjectId();
		_unk1 = unk1;
		_unk2 = unk2;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x40);
		writeD(_chaId);
		writeD(_unk1); // в снифе было 20670
		writeD(_unk2); // в снифе было 730502
	}
}