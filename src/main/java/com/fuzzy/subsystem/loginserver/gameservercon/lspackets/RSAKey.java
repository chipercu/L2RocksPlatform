package com.fuzzy.subsystem.loginserver.gameservercon.lspackets;

public class RSAKey extends ServerBasePacket
{
	public RSAKey(byte[] data)
	{
		writeC(0);
		writeB(data);
	}
}
