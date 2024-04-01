package com.fuzzy.subsystem.gameserver.clientpackets.Interface;

import com.fuzzy.subsystem.gameserver.clientpackets.L2GameClientPacket;
import com.fuzzy.subsystem.gameserver.model.L2Player;
import com.fuzzy.subsystem.gameserver.serverpackets.Interface.ConfigPacket;
import com.fuzzy.subsystem.gameserver.serverpackets.Interface.CustomFontsPacket;
import com.fuzzy.subsystem.gameserver.serverpackets.Interface.KeyPacket;
import com.fuzzy.subsystem.gameserver.serverpackets.Interface.ScreenTextInfoPacket;

//Opcodes: D0[c]:83[h]:10[d]
public class RequestInterfacePackets extends L2GameClientPacket
{
	byte[] data = null;
	int data_size;
	
	@Override
	protected void readImpl() {
		if(_buf.remaining() > 2) {
			data_size = readH();
			if(_buf.remaining() >= data_size) {
				data = new byte[data_size];
				readB(data);
			}
		}
	}

	@Override
	protected void runImpl() {
		L2Player activeChar = getClient().getActiveChar();

		if(activeChar == null)
			return;
		activeChar.sendPacket(new KeyPacket().sendKey(data, data_size));
		activeChar.sendPacket(new ConfigPacket());
		activeChar.sendPacket(new CustomFontsPacket().sendFontInfos());
		activeChar.sendPacket(new ScreenTextInfoPacket().sendTextInfos());
	}
}
