package com.fuzzy.subsystem.gameserver.clientpackets;

import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.gameserver.model.L2Player;

public class RequestRecipeShopManageQuit extends L2GameClientPacket
{
	@Override
	public void readImpl()
	{
		if(_buf.hasRemaining())
			_log.info("RequestRecipeShopManageQuit: "+readD());
	}

	@Override
	public void runImpl()
	{
		L2Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(System.currentTimeMillis() - activeChar.getLastRequestRecipeShopManageQuitPacket() < ConfigValue.RequestRecipeShopManageQuitPacketDelay)
		{
			activeChar.sendActionFailed();
			return;
		}
		activeChar.setLastRequestRecipeShopManageQuitPacket();
		if(activeChar.getDuel() != null)
		{
			activeChar.sendActionFailed();
			return;
		}

		activeChar.setPrivateStoreType(L2Player.STORE_PRIVATE_NONE);
		activeChar.broadcastUserInfo(true);
		activeChar.standUp();
	}
}