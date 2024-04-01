package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;

/**
 * Format: (ch)S
 * S: numerical password
 */
public class RequestEx2ndPasswordVerify extends L2GameClientPacket
{
	String _password;
	
	@Override
	protected void readImpl()
	{
		_password = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if(!ConfigValue.SAEnabled)
			return;
		
		getClient().getSecondaryAuth().checkPassword(_password, false);
	}
}