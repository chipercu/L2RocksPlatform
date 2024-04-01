package com.fuzzy.subsystem.gameserver.serverpackets;

public class AllianceCrest extends L2GameServerPacket
{
	private int _crestId;
	private byte[] _data;

	public AllianceCrest(int crestId, byte[] data)
	{
		_crestId = crestId;
		_data = data;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xaf);
		writeD(_crestId);
		writeD(_data.length);
		writeB(_data);
	}

	@Override
	protected boolean writeImplLindvior()
	{
		writeC(0xaf);
		writeD(0x01);
		writeD(_crestId);
		writeD(_data.length);
		writeB(_data);
		return true;
	}
}