package com.fuzzy.subsystem.gameserver.serverpackets;

import com.fuzzy.subsystem.extensions.network.MMOConnection;
import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;

public class WrappedMessage extends L2GameServerPacket
{
	final byte[] data;

	@SuppressWarnings("unchecked")
	public WrappedMessage(byte[] data, MMOConnection con)
	{
		this.data = data;
	}

	public int size()
	{
		return data.length + 2;
	}

	public byte[] getData()
	{
		return data;
	}

	public L2GameClientPacket getClientMsg()
	{
		return null;
	}

	@Override
	protected final void writeImpl()
	{
		writeB(data);
	}

	@Override
	public String getType()
	{
		return null;
	}
}