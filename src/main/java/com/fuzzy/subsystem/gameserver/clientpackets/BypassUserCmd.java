package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.extensions.multilang.CustomMessage;
import com.fuzzy.subsystem.gameserver.handler.IUserCommandHandler;
import com.fuzzy.subsystem.gameserver.handler.UserCommandHandler;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * format:  cd
 * Пример пакета по команде /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket
{
	private int _command;

	@Override
	public void readImpl()
	{
		_command = readD();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null || activeChar.is_block)
			return;

		IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);

		if(handler == null)
			activeChar.sendMessage(new CustomMessage("common.S1NotImplemented", activeChar).addString(String.valueOf(_command)));
		else
			handler.useUserCommand(_command, activeChar);
	}
}