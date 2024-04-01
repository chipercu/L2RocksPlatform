package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.gameserver.handler.AdminCommandHandler;
import com.fuzzy.subsystem.gameserver.model.L2Player;

/**
 * This class handles all GM commands triggered by //command
 */
public class SendBypassBuildCmd extends L2GameClientPacket
{
	// format: cS
	public static int GM_MESSAGE = 9;
	public static int ANNOUNCEMENT = 10;

	private String _command;

	@Override
	public void readImpl()
	{
		_command = readS();

		if(_command != null)
			_command = _command.trim();
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null || activeChar.is_block)
			return;

		String cmd = _command;

		if(!cmd.contains("admin_"))
			cmd = "admin_" + cmd;

		_log.info("SendBypassBuildCmd: "+_command);
		AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, cmd);
	}
}